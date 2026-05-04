package com.example.oreo2.dto;

import com.example.oreo2.entity.Sale;

import java.math.BigDecimal;
import java.time.Instant;

public record SaleResponse(
        String id,
        String sku,
        int units,
        BigDecimal price,
        String branch,
        Instant soldAt,
        String createdBy
) {
    public static SaleResponse from(Sale sale) {
        return new SaleResponse(
                sale.getId(),
                sale.getSku(),
                sale.getUnits(),
                sale.getPrice(),
                sale.getBranch(),
                sale.getSoldAt(),
                sale.getCreatedBy()
        );
    }
}
