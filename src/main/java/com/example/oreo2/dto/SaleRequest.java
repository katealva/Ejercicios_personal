package com.example.oreo2.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

public record SaleRequest(
        @NotBlank(message = "sku is required")
        String sku,

        @NotNull
        @Positive(message = "units must be positive")
        Integer units,

        @NotNull
        @DecimalMin(value = "0.01", message = "price must be greater than zero")
        BigDecimal price,

        @NotBlank(message = "branch is required")
        String branch,

        @NotNull(message = "soldAt is required")
        Instant soldAt
) {}
