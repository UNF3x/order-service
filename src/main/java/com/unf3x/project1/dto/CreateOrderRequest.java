package com.unf3x.project1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Request for creating a new order")
public record CreateOrderRequest(

        @Schema(
                description = "User identifier",
                example = "11111111-1111-1111-1111-111111111111"
        )
        @NotNull
        UUID userId,

        @Schema(
                description = "Total order amount",
                example = "100.50"
        )
        @NotNull
        @Positive
        BigDecimal totalAmount,

        @Schema(
                description = "Currency code (ISO 4217)",
                example = "USD"
        )
        @NotBlank
        String currency,

        @Schema(
                description = "Optional order description",
                example = "Payment for subscription"
        )
        String description
) {}