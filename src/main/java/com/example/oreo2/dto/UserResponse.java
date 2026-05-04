package com.example.oreo2.dto;

import com.example.oreo2.entity.Role;
import com.example.oreo2.entity.User;

import java.time.Instant;

public record UserResponse(
        String id,
        String username,
        String email,
        Role role,
        String branch,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getBranch(),
                user.getCreatedAt()
        );
    }
}
