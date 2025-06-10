package com.guibedan.btgpactual.orderms.listener;

import static com.guibedan.btgpactual.orderms.config.RabbitMqConfig.ORDER_CREATED_QUEUE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.guibedan.btgpactual.orderms.listener.dto.OrderCreatedEvent;
import com.guibedan.btgpactual.orderms.service.OrderService;

@Component
public class OrderCreatedListener {

	private final Logger logger = LoggerFactory.getLogger(OrderCreatedListener.class);

	private final OrderService orderService;

	public OrderCreatedListener(OrderService orderService) {
		this.orderService = orderService;
	}

	@RabbitListener(queues = ORDER_CREATED_QUEUE)
	public void listen(Message<OrderCreatedEvent> message) {
		logger.info("Received order created event: {}", message);
		orderService.save(message.getPayload());
		logger.info("Order created successfully");
	}

}
