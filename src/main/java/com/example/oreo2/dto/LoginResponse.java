package com.example.oreo2.dto;

import com.example.oreo2.entity.Role;

public record LoginResponse(
        String token,
        long expiresIn,
        Role role,
        String branch
) {}
