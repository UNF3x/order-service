package com.unf3x.project1.controller;

import com.unf3x.project1.dto.CreateOrderRequest;
import com.unf3x.project1.dto.UpdateOrderRequest;
import com.unf3x.project1.entity.Order;
import com.unf3x.project1.entity.OrderStatus;
import com.unf3x.project1.repository.OrderRepository;
import com.unf3x.project1.support.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/v1/orders";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void create_shouldReturnCreatedOrder() throws Exception {
        UUID userId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(
                userId,
                new BigDecimal("100.00"),
                "USD",
                "test order"
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(100.00))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.description").value("test order"));
    }

    @Test
    void getById_shouldReturnOrder() throws Exception {
        Order order = orderRepository.save(
                Order.create(
                        UUID.randomUUID(),
                        OrderStatus.CREATED,
                        new BigDecimal("250.00"),
                        "EUR",
                        "existing order"
                )
        );

        mockMvc.perform(get(BASE_URL + "/" + order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(250.00))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.description").value("existing order"));
    }

    @Test
    void getById_shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get(BASE_URL + "/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_shouldReturnUpdatedOrder() throws Exception {
        Order order = orderRepository.save(
                Order.create(
                        UUID.randomUUID(),
                        OrderStatus.CREATED,
                        new BigDecimal("100.00"),
                        "USD",
                        "before update"
                )
        );

        UpdateOrderRequest request = new UpdateOrderRequest(
                new BigDecimal("150.00"),
                "GBP",
                "after update"
        );

        mockMvc.perform(put(BASE_URL + "/" + order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()))
                .andExpect(jsonPath("$.totalAmount").value(150.00))
                .andExpect(jsonPath("$.currency").value("GBP"))
                .andExpect(jsonPath("$.description").value("after update"));
    }

    @Test
    void delete_shouldSoftDeleteOrderAndReturnNoContent() throws Exception {
        Order order = orderRepository.save(
                Order.create(
                        UUID.randomUUID(),
                        OrderStatus.CREATED,
                        new BigDecimal("500.00"),
                        "USD",
                        "to delete"
                )
        );

        mockMvc.perform(delete(BASE_URL + "/" + order.getId()))
                .andExpect(status().isNoContent());

        Order deletedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertNotNull(deletedOrder.getDeletedAt());
    }

    @Test
    void getAll_shouldReturnPagedOrders() throws Exception {
        orderRepository.save(
                Order.create(
                        UUID.randomUUID(),
                        OrderStatus.CREATED,
                        new BigDecimal("100.00"),
                        "USD",
                        "order 1"
                )
        );

        orderRepository.save(
                Order.create(
                        UUID.randomUUID(),
                        OrderStatus.PAID,
                        new BigDecimal("200.00"),
                        "EUR",
                        "order 2"
                )
        );

        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.pageSize").value(20))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.items.length()").value(2));
    }
}