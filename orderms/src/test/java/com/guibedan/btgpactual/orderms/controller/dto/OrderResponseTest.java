package com.guibedan.btgpactual.orderms.controller.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.guibedan.btgpactual.orderms.factory.OrderEntityFactory;

class OrderResponseTest {

    @Nested
    class FromEntity {

        @Test
        void shouldMapCorrectly() {
            var input = OrderEntityFactory.build();

            var output = OrderResponse.fromEntity(input);

            assertEquals(input.getOrderId(), output.orderId());
            assertEquals(input.getCustomerId(), output.customerId());
            assertEquals(input.getTotal(), output.total());
        }

    }

}