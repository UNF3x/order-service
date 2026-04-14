package com.unf3x.project1.dto;

import java.util.List;

public record OrderPageResponse(
        List<OrderResponse> items,
        int page,
        int pageSize,
        long total
) {
}