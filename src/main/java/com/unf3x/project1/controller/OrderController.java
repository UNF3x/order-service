package com.unf3x.project1.controller;

import com.unf3x.project1.dto.CreateOrderRequest;
import com.unf3x.project1.dto.OrderPageResponse;
import com.unf3x.project1.dto.OrderResponse;
import com.unf3x.project1.dto.UpdateOrderRequest;
import com.unf3x.project1.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Tag(name = "Orders", description = "Operations for managing orders")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Create order",
            description = "Creates a new order with status CREATED"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(request));
    }

    @Operation(
            summary = "Get order by id",
            description = "Returns an order by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameter"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(
            @Parameter(description = "Order UUID", required = true)
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @Operation(
            summary = "Get orders with offset pagination",
            description = "Returns paginated list of orders with optional filtering by status and userId"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderPageResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameter")
    })
    @GetMapping
    public ResponseEntity<OrderPageResponse> getAll(
            @Parameter(description = "Page number, starts from 0", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Page size, allowed range: 1..100", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize,

            @Parameter(description = "Order status filter", example = "CREATED")
            @RequestParam(required = false) String status,

            @Parameter(description = "User UUID filter", example = "11111111-1111-1111-1111-111111111111")
            @RequestParam(required = false) UUID userId
    ) {
        return ResponseEntity.ok(orderService.getAll(page, pageSize, status, userId));
    }

    @Operation(
            summary = "Get orders with keyset pagination",
            description = "Returns next portion of orders using keyset pagination based on createdAt and id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderPageResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameter")
    })
    @GetMapping("/keyset")
    public ResponseEntity<OrderPageResponse> getAllByKeyset(
            @Parameter(
                    description = "Cursor field: createdAt of the last item from previous page",
                    example = "2026-04-14T04:48:56"
            )
            @RequestParam(required = false) LocalDateTime lastCreatedAt,

            @Parameter(
                    description = "Cursor field: id of the last item from previous page",
                    example = "36a1f4ae-39ac-4940-be6d-3bca10f7d49a"
            )
            @RequestParam(required = false) UUID lastId,

            @Parameter(description = "Maximum number of items to return, allowed range: 1..100", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit
    ) {
        return ResponseEntity.ok(orderService.getAllByKeyset(lastCreatedAt, lastId, limit));
    }

    @Operation(
            summary = "Update order",
            description = "Updates editable fields of an existing order"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Validation error or invalid request parameter"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> update(
            @Parameter(description = "Order UUID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderRequest request
    ) {
        return ResponseEntity.ok(orderService.update(id, request));
    }

    @Operation(
            summary = "Delete order",
            description = "Soft deletes an order by setting deletedAt"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameter"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Order UUID", required = true)
            @PathVariable UUID id
    ) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}