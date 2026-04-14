package com.unf3x.project1.service.impl;

import com.unf3x.project1.dto.CreateOrderRequest;
import com.unf3x.project1.dto.OrderPageResponse;
import com.unf3x.project1.dto.OrderResponse;
import com.unf3x.project1.dto.UpdateOrderRequest;
import com.unf3x.project1.entity.Order;
import com.unf3x.project1.entity.OrderStatus;
import com.unf3x.project1.exception.OrderNotFoundException;
import com.unf3x.project1.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void create_shouldCreateOrderWithCreatedStatus() {
        UUID userId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(
                userId,
                new BigDecimal("100.00"),
                "USD",
                "test order"
        );

        Order savedOrder = buildPersistedOrder(
                UUID.randomUUID(),
                userId,
                OrderStatus.CREATED,
                new BigDecimal("100.00"),
                "USD",
                "test order"
        );

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderResponse response = orderService.create(request);

        assertNotNull(response);
        assertEquals(userId, response.userId());
        assertEquals(OrderStatus.CREATED, response.status());
        assertEquals(new BigDecimal("100.00"), response.totalAmount());
        assertEquals("USD", response.currency());
        assertEquals("test order", response.description());

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());

        Order orderToSave = captor.getValue();
        assertEquals(userId, orderToSave.getUserId());
        assertEquals(OrderStatus.CREATED, orderToSave.getStatus());
        assertEquals(new BigDecimal("100.00"), orderToSave.getTotalAmount());
        assertEquals("USD", orderToSave.getCurrency());
        assertEquals("test order", orderToSave.getDescription());
    }

    @Test
    void getById_shouldReturnOrderWhenExists() {
        UUID orderId = UUID.randomUUID();

        Order order = buildPersistedOrder(
                orderId,
                UUID.randomUUID(),
                OrderStatus.CREATED,
                new BigDecimal("250.00"),
                "EUR",
                "existing order"
        );

        when(orderRepository.findOne(org.mockito.ArgumentMatchers.<Specification<Order>>any()))
                .thenReturn(Optional.of(order));

        OrderResponse response = orderService.getById(orderId);

        assertNotNull(response);
        assertEquals(orderId, response.id());
        assertEquals(OrderStatus.CREATED, response.status());
        assertEquals(new BigDecimal("250.00"), response.totalAmount());
        assertEquals("EUR", response.currency());

        verify(orderRepository).findOne(org.mockito.ArgumentMatchers.<Specification<Order>>any());
    }

    @Test
    void getById_shouldThrowWhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findOne(org.mockito.ArgumentMatchers.<Specification<Order>>any()))
                .thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getById(orderId));

        verify(orderRepository).findOne(org.mockito.ArgumentMatchers.<Specification<Order>>any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateFields() {
        UUID orderId = UUID.randomUUID();

        Order order = buildPersistedOrder(
                orderId,
                UUID.randomUUID(),
                OrderStatus.CREATED,
                new BigDecimal("100.00"),
                "USD",
                "before update"
        );

        UpdateOrderRequest request = new UpdateOrderRequest(
                new BigDecimal("150.00"),
                "GBP",
                "after update"
        );

        when(orderRepository.findOne(org.mockito.ArgumentMatchers.<Specification<Order>>any()))
                .thenReturn(Optional.of(order));

        OrderResponse response = orderService.update(orderId, request);

        assertNotNull(response);
        assertEquals(new BigDecimal("150.00"), response.totalAmount());
        assertEquals("GBP", response.currency());
        assertEquals("after update", response.description());

        assertEquals(new BigDecimal("150.00"), order.getTotalAmount());
        assertEquals("GBP", order.getCurrency());
        assertEquals("after update", order.getDescription());

        verify(orderRepository).findOne(org.mockito.ArgumentMatchers.<Specification<Order>>any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowWhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();

        UpdateOrderRequest request = new UpdateOrderRequest(
                new BigDecimal("150.00"),
                "GBP",
                "after update"
        );

        when(orderRepository.findOne(org.mockito.ArgumentMatchers.<Specification<Order>>any()))
                .thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.update(orderId, request));

        verify(orderRepository).findOne(org.mockito.ArgumentMatchers.<Specification<Order>>any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void delete_shouldSetDeletedAt() {
        UUID orderId = UUID.randomUUID();

        Order order = buildPersistedOrder(
                orderId,
                UUID.randomUUID(),
                OrderStatus.CREATED,
                new BigDecimal("500.00"),
                "USD",
                "to delete"
        );

        assertNull(order.getDeletedAt());

        when(orderRepository.findOne(org.mockito.ArgumentMatchers.<Specification<Order>>any()))
                .thenReturn(Optional.of(order));

        orderService.delete(orderId);

        assertNotNull(order.getDeletedAt());

        verify(orderRepository).findOne(org.mockito.ArgumentMatchers.<Specification<Order>>any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void delete_shouldThrowWhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findOne(org.mockito.ArgumentMatchers.<Specification<Order>>any()))
                .thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.delete(orderId));

        verify(orderRepository).findOne(org.mockito.ArgumentMatchers.<Specification<Order>>any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getAll_shouldReturnPagedResponse() {
        Order order1 = buildPersistedOrder(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OrderStatus.CREATED,
                new BigDecimal("100.00"),
                "USD",
                "order 1"
        );

        Order order2 = buildPersistedOrder(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OrderStatus.CREATED,
                new BigDecimal("200.00"),
                "EUR",
                "order 2"
        );

        Page<Order> page = new PageImpl<>(
                List.of(order1, order2),
                PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt")),
                2
        );

        when(orderRepository.findAll(
                org.mockito.ArgumentMatchers.<Specification<Order>>any(),
                any(Pageable.class)
        )).thenReturn(page);

        OrderPageResponse response = orderService.getAll(0, 20, "CREATED", null);

        assertNotNull(response);
        assertEquals(0, response.page());
        assertEquals(20, response.pageSize());
        assertEquals(2, response.total());
        assertEquals(2, response.items().size());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(orderRepository).findAll(
                org.mockito.ArgumentMatchers.<Specification<Order>>any(),
                pageableCaptor.capture()
        );

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
        assertNotNull(pageable.getSort().getOrderFor("createdAt"));
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor("createdAt").getDirection());
    }

    @Test
    void getAllByKeyset_shouldReturnResponse() {
        Order order1 = buildPersistedOrder(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OrderStatus.CREATED,
                new BigDecimal("100.00"),
                "USD",
                "order 1"
        );

        Order order2 = buildPersistedOrder(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OrderStatus.CREATED,
                new BigDecimal("200.00"),
                "EUR",
                "order 2"
        );

        Page<Order> page = new PageImpl<>(
                List.of(order1, order2),
                PageRequest.of(0, 10),
                2
        );

        when(orderRepository.findAll(
                org.mockito.ArgumentMatchers.<Specification<Order>>any(),
                any(Pageable.class)
        )).thenReturn(page);

        OrderPageResponse response = orderService.getAllByKeyset(null, null, 10);

        assertNotNull(response);
        assertEquals(0, response.page());
        assertEquals(10, response.pageSize());
        assertEquals(2, response.total());
        assertEquals(2, response.items().size());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(orderRepository).findAll(
                org.mockito.ArgumentMatchers.<Specification<Order>>any(),
                pageableCaptor.capture()
        );

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
        assertNotNull(pageable.getSort().getOrderFor("createdAt"));
        assertNotNull(pageable.getSort().getOrderFor("id"));
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor("createdAt").getDirection());
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor("id").getDirection());
    }

    private Order buildPersistedOrder(
            UUID id,
            UUID userId,
            OrderStatus status,
            BigDecimal totalAmount,
            String currency,
            String description
    ) {
        Order order = Order.create(userId, status, totalAmount, currency, description);

        ReflectionTestUtils.setField(order, "id", id);
        ReflectionTestUtils.setField(order, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(order, "updatedAt", LocalDateTime.now());
        ReflectionTestUtils.setField(order, "version", 0L);

        return order;
    }
}