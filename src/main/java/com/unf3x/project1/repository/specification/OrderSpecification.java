package com.unf3x.project1.repository.specification;

import com.unf3x.project1.entity.Order;
import com.unf3x.project1.entity.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

public final class OrderSpecification {

    private OrderSpecification() {
    }

    public static Specification<Order> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Order> keyset(LocalDateTime lastCreatedAt, UUID lastId) {
        return (root, query, cb) -> {
            if (lastCreatedAt == null || lastId == null) {
                return cb.conjunction();
            }

            return cb.or(
                    cb.lessThan(root.get("createdAt"), lastCreatedAt),
                    cb.and(
                            cb.equal(root.get("createdAt"), lastCreatedAt),
                            cb.lessThan(root.get("id"), lastId)
                    )
            );
        };
    }

    public static Specification<Order> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isBlank()) {
                return cb.conjunction();
            }

            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                return cb.equal(root.get("status"), orderStatus);
            } catch (IllegalArgumentException ex) {
                return cb.disjunction();
            }
        };
    }

    public static Specification<Order> hasUserId(UUID userId) {
        return (root, query, cb) ->
                userId == null
                        ? cb.conjunction()
                        : cb.equal(root.get("userId"), userId);
    }
}
