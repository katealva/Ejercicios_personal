package com.example.oreo2.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ReportRequestedEvent {
    private String requestId;
    private LocalDate from;
    private LocalDate to;
    private String branch;
    private String emailTo;
    private String requestedByUsername;
    private Instant requestedAt;
}
