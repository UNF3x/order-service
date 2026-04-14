package com.unf3x.project1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_orders_user_id", columnList = "user_id"),
                @Index(name = "idx_orders_status", columnList = "status"),
                @Index(name = "idx_orders_created_at", columnList = "created_at"),
                @Index(name = "idx_orders_deleted_at", columnList = "deleted_at")
        }
)
@Getter
@NoArgsConstructor
public class Order {

    @Id
    private UUID id;

    @Setter
    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Setter
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Setter
    @Column(nullable = false, length = 3)
    private String currency;

    @Setter
    @Column(length = 1000)
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Setter
    private LocalDateTime deletedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.id = UUID.randomUUID();
        this.createdAt = now;
        this.updatedAt = now;
        this.version = 0L;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static Order create(
            UUID userId,
            OrderStatus status,
            BigDecimal totalAmount,
            String currency,
            String description
    ) {
        Order order = new Order();
        order.userId = userId;
        order.status = status;
        order.totalAmount = totalAmount;
        order.currency = currency;
        order.description = description;
        return order;
    }
}