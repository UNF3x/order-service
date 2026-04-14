package com.unf3x.project1.mapper;

import com.unf3x.project1.dto.OrderResponse;
import com.unf3x.project1.entity.Order;

public class OrderMapper {

    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getDescription(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}