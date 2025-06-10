package com.guibedan.btgpactual.orderms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.math.BigDecimal;

import org.bson.Document;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import com.guibedan.btgpactual.orderms.entity.OrderEntity;
import com.guibedan.btgpactual.orderms.factory.OrderCreatedEventFactory;
import com.guibedan.btgpactual.orderms.factory.OrderEntityFactory;
import com.guibedan.btgpactual.orderms.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	OrderRepository orderRepository;

	@Mock
	MongoTemplate mongoTemplate;

	@InjectMocks
	OrderService orderService;

	@Captor
	ArgumentCaptor<OrderEntity> orderEntityCaptor;

	@Captor
	ArgumentCaptor<Aggregation> aggregationCaptor;

	@Nested
	class Save {

		@Test
		void shouldCallRepositorySave() {
			// arrange
			var event = OrderCreatedEventFactory.buildWithOneItem();
			// act
			orderService.save(event);
			// assert
			verify(orderRepository, times(1)).save(any());
		}

		@Test
		void shouldMapEventToEntityWithSuccess() {
			// arrange
			var event = OrderCreatedEventFactory.buildWithOneItem();
			// act
			orderService.save(event);
			// assert
			verify(orderRepository, times(1)).save(orderEntityCaptor.capture());

			var entity = orderEntityCaptor.getValue();
			assertEquals(event.codigoPedido(), entity.getOrderId());
			assertEquals(event.codigoCliente(), entity.getCustomerId());
			assertNotNull(entity.getTotal());
			assertEquals(event.itens().getFirst().produto(), entity.getItems().getFirst().getProduct());
			assertEquals(event.itens().getFirst().preco(), entity.getItems().getFirst().getPrice());
			assertEquals(event.itens().getFirst().quantidade(), entity.getItems().getFirst().getQuantity());
		}

		@Test
		void shouldCalculateOrderTotalWithSuccess() {
			// arrange
			var event = OrderCreatedEventFactory.buildWithTwoItens();
			var totalItem1 = event.itens().getFirst().preco()
					.multiply(BigDecimal.valueOf(event.itens().getFirst().quantidade()));
			var totalItem2 = event.itens().getLast().preco()
					.multiply(BigDecimal.valueOf(event.itens().getLast().quantidade()));
			var orderTotal = totalItem1.add(totalItem2);
			// act
			orderService.save(event);
			// assert
			verify(orderRepository, times(1)).save(orderEntityCaptor.capture());

			var entity = orderEntityCaptor.getValue();
			assertNotNull(entity.getTotal());
			assertEquals(orderTotal, entity.getTotal());
		}

	}

	@Nested
	class FindAllByCustomerId {

		@Test
		void shouldCallRepository() {
			// arrange
			var customerId = 1L;
			var pageRequest = PageRequest.of(0, 10);
			doReturn(OrderEntityFactory.buildWithPage()).when(orderRepository).findAllByCustomerId(eq(customerId),
					eq(pageRequest));
			// act
			var response = orderService.findAllByCustomerId(customerId, pageRequest);
			// assert
			verify(orderRepository, times(1)).findAllByCustomerId(eq(customerId), eq(pageRequest));
		}

		@Test
		void shouldMapResponse() {
			// arrange
			var customerId = 1L;
			var pageRequest = PageRequest.of(0, 10);
			var page = OrderEntityFactory.buildWithPage();
			doReturn(page).when(orderRepository).findAllByCustomerId(anyLong(), any());
			// act
			var response = orderService.findAllByCustomerId(customerId, pageRequest);
			// assert
			assertEquals(page.getTotalPages(), response.getTotalPages());
			assertEquals(page.getNumber(), response.getNumber());
			assertEquals(page.getSize(), response.getSize());
			assertEquals(page.getTotalElements(), response.getTotalElements());

			assertEquals(page.getContent().getFirst().getCustomerId(), response.getContent().getFirst().customerId());
			assertEquals(page.getContent().getFirst().getOrderId(), response.getContent().getFirst().orderId());
			assertEquals(page.getContent().getFirst().getTotal(), response.getContent().getFirst().total());
		}

	}

	@Nested
	class FindTotalOnOrdersByCustomerId {

		@Test
		void shouldCallMongoTemplate() {
			// arrange
			var customerId = 1L;
			var totalExpected = BigDecimal.valueOf(1);
			var aggregationResult = mock(AggregationResults.class);
			doReturn(new Document("total", totalExpected)).when(aggregationResult).getUniqueMappedResult();
			doReturn(aggregationResult).when(mongoTemplate).aggregate(any(Aggregation.class), anyString(),
					eq(Document.class));

			// act
			var total = orderService.findTotalOnOrdersByCustomerId(customerId);

			// assert
			verify(mongoTemplate, times(1)).aggregate(any(Aggregation.class), anyString(), eq(Document.class));
			assertEquals(totalExpected, total);
		}

		@Test
		void shouldUserCorrectAggregation() {
			// arrange
			var customerId = 1L;
			var totalExpected = BigDecimal.valueOf(1);
			var aggregationResult = mock(AggregationResults.class);
			doReturn(new Document("total", totalExpected)).when(aggregationResult).getUniqueMappedResult();
			doReturn(aggregationResult).when(mongoTemplate).aggregate(aggregationCaptor.capture(), anyString(),
					eq(Document.class));

			// act
			orderService.findTotalOnOrdersByCustomerId(customerId);

			// assert
			var aggregation = aggregationCaptor.getValue();
			var aggregationExpected = newAggregation(match(Criteria.where("customerId").is(customerId)),
					group().sum("total").as("total"));
			assertEquals(aggregationExpected.toString(), aggregation.toString());
		}

		@Test
		void shouldQueryCorrectTable() {
			// arrange
			var tbName = "tb_orders";
			var customerId = 1L;
			var totalExpected = BigDecimal.valueOf(1);
			var aggregationResult = mock(AggregationResults.class);
			doReturn(new Document("total", totalExpected)).when(aggregationResult).getUniqueMappedResult();
			doReturn(aggregationResult).when(mongoTemplate).aggregate(any(Aggregation.class), eq(tbName),
					eq(Document.class));

			// act
			orderService.findTotalOnOrdersByCustomerId(customerId);

			// assert
			verify(mongoTemplate, times(1)).aggregate(any(Aggregation.class), eq(tbName), eq(Document.class));
		}

	}

}