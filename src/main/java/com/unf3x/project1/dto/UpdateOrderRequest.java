package com.unf3x.project1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Request for updating an existing order")
public record UpdateOrderRequest(

        @Schema(
                description = "Updated total amount",
                example = "150.00"
        )
        @NotNull
        @Positive
        BigDecimal totalAmount,

        @Schema(
                description = "Updated currency",
                example = "EUR"
        )
        @NotBlank
        String currency,

        @Schema(
                description = "Updated description",
                example = "Updated payment"
        )
        String description
) {}