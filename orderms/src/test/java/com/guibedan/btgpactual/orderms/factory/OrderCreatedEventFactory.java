package com.guibedan.btgpactual.orderms.factory;

import java.math.BigDecimal;
import java.util.List;

import com.guibedan.btgpactual.orderms.listener.dto.OrderCreatedEvent;
import com.guibedan.btgpactual.orderms.listener.dto.OrderItemEvent;

public class OrderCreatedEventFactory {
	public static OrderCreatedEvent buildWithOneItem() {
		var itens = new OrderItemEvent("notebook", 1, BigDecimal.valueOf(20.50));
		return new OrderCreatedEvent(1L, 2L, List.of(itens));
	}

	public static OrderCreatedEvent buildWithTwoItens() {
		var item1 = new OrderItemEvent("notebook", 1, BigDecimal.valueOf(20.50));
		var item2 = new OrderItemEvent("mouse", 1, BigDecimal.valueOf(35.25));
		return new OrderCreatedEvent(1L, 2L, List.of(item1, item2));
	}
}
