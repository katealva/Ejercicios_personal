package com.example.oreo2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record WeeklySummaryRequest(
        LocalDate from,
        LocalDate to,
        String branch,
        @NotBlank(message = "emailTo es obligatorio")
        @Email(message = "emailTo debe tener formato de email valido")
        String emailTo
) {}
