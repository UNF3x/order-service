package com.unf3x.project1.service;

import com.unf3x.project1.dto.CreateOrderRequest;
import com.unf3x.project1.dto.OrderPageResponse;
import com.unf3x.project1.dto.OrderResponse;
import com.unf3x.project1.dto.UpdateOrderRequest;

import java.time.LocalDateTime;
import java.util.UUID;

public interface OrderService {

    OrderResponse create(CreateOrderRequest request);

    OrderResponse getById(UUID id);

    OrderPageResponse getAll(int page, int pageSize, String status, UUID userId);

    OrderPageResponse getAllByKeyset(LocalDateTime lastCreatedAt, UUID lastId, int limit);

    OrderResponse update(UUID id, UpdateOrderRequest request);

    void delete(UUID id);
}