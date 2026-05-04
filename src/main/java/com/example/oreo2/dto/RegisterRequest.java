package com.example.oreo2.dto;

import com.example.oreo2.entity.Role;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank
        @Size(min = 3, max = 30, message = "username must be 3-30 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_.]+$",
                 message = "username: only letters, digits, _ and . allowed")
        String username,

        @NotBlank
        @Email(message = "must be a valid email")
        String email,

        @NotBlank
        @Size(min = 8, message = "password: minimum 8 characters")
        String password,

        @NotNull(message = "role is required")
        Role role,

        String branch
) {}
