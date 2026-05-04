package com.example.oreo2.dto;

import java.time.Instant;

public record WeeklySummaryResponse(
        String requestId,
        String status,
        String message,
        String estimatedTime,
        Instant requestedAt
) {}
