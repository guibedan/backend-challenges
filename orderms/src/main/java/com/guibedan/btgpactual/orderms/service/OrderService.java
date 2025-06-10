package com.guibedan.btgpactual.orderms.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.guibedan.btgpactual.orderms.controller.dto.OrderResponse;
import com.guibedan.btgpactual.orderms.entity.OrderEntity;
import com.guibedan.btgpactual.orderms.entity.OrderItem;
import com.guibedan.btgpactual.orderms.listener.dto.OrderCreatedEvent;
import com.guibedan.btgpactual.orderms.repository.OrderRepository;

@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final MongoTemplate mongoTemplate;

	public OrderService(OrderRepository orderRepository, MongoTemplate mongoTemplate) {
		this.orderRepository = orderRepository;
		this.mongoTemplate = mongoTemplate;
	}

	public void save(OrderCreatedEvent event) {
		var entity = new OrderEntity();
		entity.setOrderId(event.codigoPedido());
		entity.setCustomerId(event.codigoCliente());
		entity.setItems(getOrderItems(event));
		entity.setTotal(getTotal(event));

		orderRepository.save(entity);
	}

	private BigDecimal getTotal(OrderCreatedEvent event) {
		return event.itens().stream().map(x -> x.preco().multiply(BigDecimal.valueOf(x.quantidade())))
				.reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
	}

	private static List<OrderItem> getOrderItems(OrderCreatedEvent event) {
		return event.itens().stream().map(x -> new OrderItem(x.produto(), x.quantidade(), x.preco())).toList();
	}

	public Page<OrderResponse> findAllByCustomerId(Long customerId, PageRequest pageRequest) {
		var orders = orderRepository.findAllByCustomerId(customerId, pageRequest);
		return orders.map(OrderResponse::fromEntity);
	}

	public BigDecimal findTotalOnOrdersByCustomerId(Long customerId) {
		var aggregations = newAggregation(match(Criteria.where("customerId").is(customerId)),
				group().sum("total").as("total"));

		var response = mongoTemplate.aggregate(aggregations, "tb_orders", Document.class);
		return new BigDecimal(Objects.requireNonNull(response.getUniqueMappedResult()).get("total").toString());
	}

}
