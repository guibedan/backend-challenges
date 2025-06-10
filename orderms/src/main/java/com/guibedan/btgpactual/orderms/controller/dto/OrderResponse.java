package com.guibedan.btgpactual.orderms.controller.dto;

import java.math.BigDecimal;

import com.guibedan.btgpactual.orderms.entity.OrderEntity;

public record OrderResponse(Long orderId, Long customerId, BigDecimal total) {

	public static OrderResponse fromEntity(OrderEntity order) {
		return new OrderResponse(order.getOrderId(), order.getCustomerId(), order.getTotal());
	}

}
