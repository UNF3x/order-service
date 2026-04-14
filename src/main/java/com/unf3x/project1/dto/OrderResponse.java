package com.unf3x.project1.dto;

import com.unf3x.project1.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Order response DTO")
public record OrderResponse(

        @Schema(
                description = "Order ID",
                example = "36a1f4ae-39ac-4940-be6d-3bca10f7d49a"
        )
        UUID id,

        @Schema(
                description = "User ID",
                example = "11111111-1111-1111-1111-111111111111"
        )
        UUID userId,

        @Schema(
                description = "Order status",
                example = "CREATED"
        )
        OrderStatus status,

        @Schema(
                description = "Total amount",
                example = "100.50"
        )
        BigDecimal totalAmount,

        @Schema(
                description = "Currency",
                example = "USD"
        )
        String currency,

        @Schema(
                description = "Description",
                example = "Payment for subscription"
        )
        String description,

        @Schema(
                description = "Creation timestamp",
                example = "2026-04-14T04:48:56"
        )
        LocalDateTime createdAt,

        @Schema(
                description = "Last update timestamp",
                example = "2026-04-14T05:10:12"
        )
        LocalDateTime updatedAt
) {}