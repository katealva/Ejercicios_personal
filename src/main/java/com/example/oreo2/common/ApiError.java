package com.example.oreo2.common;

import java.time.Instant;

public record ApiError(
        String error,
        String message,
        Instant timestamp,
        String path
) {
    public static ApiError of(String error, String message, String path) {
        return new ApiError(error, message, Instant.now(), path);
    }
}