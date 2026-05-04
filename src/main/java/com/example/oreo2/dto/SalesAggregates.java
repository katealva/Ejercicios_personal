package com.example.oreo2.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesAggregates(
        int totalUnits,
        BigDecimal totalRevenue,
        String topSku,
        String topBranch,
        int salesCount,
        LocalDate from,
        LocalDate to
) {
    public static SalesAggregates empty(LocalDate from, LocalDate to) {
        return new SalesAggregates(0, BigDecimal.ZERO, null, null, 0, from, to);
    }
}
