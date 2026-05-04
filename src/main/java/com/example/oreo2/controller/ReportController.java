package com.example.oreo2.controller;

import com.example.oreo2.common.ForbiddenException;
import com.example.oreo2.dto.WeeklySummaryRequest;
import com.example.oreo2.dto.WeeklySummaryResponse;
import com.example.oreo2.entity.Role;
import com.example.oreo2.entity.User;
import com.example.oreo2.event.ReportRequestedEvent;
import com.example.oreo2.security.CurrentUserProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/sales/summary")
@RequiredArgsConstructor
public class ReportController {

    private final ApplicationEventPublisher eventPublisher;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping("/weekly")
    public ResponseEntity<WeeklySummaryResponse> requestWeeklySummary(
            @Valid @RequestBody WeeklySummaryRequest request) {

        LocalDate to = request.to() != null ? request.to() : LocalDate.now();
        LocalDate from = request.from() != null ? request.from() : to.minusDays(7);

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' no puede ser posterior a 'to'");
        }

        User current = currentUserProvider.getCurrentUser();
        String resolvedBranch = resolveBranchOrFail(current, request.branch());

        String requestId = "req_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        Instant now = Instant.now();

        ReportRequestedEvent event = new ReportRequestedEvent(
                requestId, from, to, resolvedBranch, request.emailTo(), current.getUsername(), now);

        log.info("Publicando ReportRequestedEvent {} (usuario={}, branch={}, emailTo={})",
                requestId, current.getUsername(), resolvedBranch, request.emailTo());

        eventPublisher.publishEvent(event);

        WeeklySummaryResponse body = new WeeklySummaryResponse(
                requestId,
                "PROCESSING",
                "Su solicitud de reporte esta siendo procesada. Recibira el resumen en " +
                        request.emailTo() + " en unos momentos.",
                "30-60 segundos",
                now
        );

        return ResponseEntity.accepted().body(body);
    }

    private String resolveBranchOrFail(User current, String requestedBranch) {
        if (current.getRole() == Role.BRANCH) {
            String userBranch = current.getBranch();
            if (requestedBranch != null && !requestedBranch.equals(userBranch)) {
                throw new ForbiddenException(
                        "Usuario BRANCH solo puede solicitar resumenes de su propia sucursal: " + userBranch);
            }
            return userBranch;
        }
        return requestedBranch;
    }
}
