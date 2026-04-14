package com.unf3x.project1.service.impl;

import com.unf3x.project1.dto.CreateOrderRequest;
import com.unf3x.project1.dto.OrderPageResponse;
import com.unf3x.project1.dto.OrderResponse;
import com.unf3x.project1.dto.UpdateOrderRequest;
import com.unf3x.project1.entity.Order;
import com.unf3x.project1.entity.OrderStatus;
import com.unf3x.project1.exception.OrderNotFoundException;
import com.unf3x.project1.mapper.OrderMapper;
import com.unf3x.project1.repository.OrderRepository;
import com.unf3x.project1.repository.specification.OrderSpecification;
import com.unf3x.project1.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        log.info("Creating order. userId={}, totalAmount={}, currency={}",
                request.userId(), request.totalAmount(), request.currency());

        Order order = Order.create(
                request.userId(),
                OrderStatus.CREATED,
                request.totalAmount(),
                request.currency(),
                request.description()
        );

        Order savedOrder = orderRepository.save(order);

        log.info("Order created successfully. id={}, userId={}, status={}",
                savedOrder.getId(), savedOrder.getUserId(), savedOrder.getStatus());

        return OrderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(UUID id) {
        log.info("Fetching order by id. id={}", id);

        Order order = findActiveOrderById(id);

        log.info("Order fetched successfully. id={}, status={}", order.getId(), order.getStatus());

        return OrderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderPageResponse getAll(int page, int pageSize, String status, UUID userId) {
        log.info("Fetching orders with filters. page={}, pageSize={}, status={}, userId={}",
                page, pageSize, status, userId);

        PageRequest pageRequest = PageRequest.of(page, pageSize, DEFAULT_SORT);

        Specification<Order> specification = OrderSpecification.notDeleted()
                .and(OrderSpecification.hasStatus(status))
                .and(OrderSpecification.hasUserId(userId));

        Page<Order> orderPage = orderRepository.findAll(specification, pageRequest);

        List<OrderResponse> items = orderPage.getContent()
                .stream()
                .map(OrderMapper::toResponse)
                .toList();

        log.info("Orders fetched successfully. total={}, items={}",
                orderPage.getTotalElements(), items.size());

        return new OrderPageResponse(
                items,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements()
        );
    }

    @Override
    @Transactional
    public OrderResponse update(UUID id, UpdateOrderRequest request) {
        log.info("Updating order. id={}, totalAmount={}, currency={}",
                id, request.totalAmount(), request.currency());

        Order order = findActiveOrderById(id);

        order.setTotalAmount(request.totalAmount());
        order.setCurrency(request.currency());
        order.setDescription(request.description());

        log.info("Order updated successfully. id={}", order.getId());

        return OrderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting order (soft delete). id={}", id);

        Order order = findActiveOrderById(id);
        order.setDeletedAt(LocalDateTime.now());

        log.info("Order soft deleted successfully. id={}", order.getId());
    }

    private Order findActiveOrderById(UUID id) {
        Specification<Order> specification = OrderSpecification.notDeleted()
                .and((root, query, cb) -> cb.equal(root.get("id"), id));

        return orderRepository.findOne(specification)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderPageResponse getAllByKeyset(LocalDateTime lastCreatedAt, UUID lastId, int limit) {
        log.info("Fetching orders by keyset. lastCreatedAt={}, lastId={}, limit={}",
                lastCreatedAt, lastId, limit);

        PageRequest pageRequest = PageRequest.of(
                0,
                limit,
                Sort.by(Sort.Direction.DESC, "createdAt")
                        .and(Sort.by(Sort.Direction.DESC, "id"))
        );

        Page<Order> orderPage = orderRepository.findAll(
                OrderSpecification.notDeleted()
                        .and(OrderSpecification.keyset(lastCreatedAt, lastId)),
                pageRequest
        );

        List<OrderResponse> items = orderPage.getContent()
                .stream()
                .map(OrderMapper::toResponse)
                .toList();

        log.info("Orders keyset page fetched successfully. items={}", items.size());

        return new OrderPageResponse(
                items,
                0,
                limit,
                items.size()
        );
    }
}