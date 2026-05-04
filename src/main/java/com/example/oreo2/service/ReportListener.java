package com.example.oreo2.service;

import com.example.oreo2.dto.SalesAggregates;
import com.example.oreo2.event.ReportRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportListener {

    private final SalesAggregationService salesAggregationService;
    private final LlmClient llmClient;
    private final EmailService emailService;

    @Async("reportTaskExecutor")
    @EventListener
    public void handleReportRequest(ReportRequestedEvent ev) {
        log.info("[{}] Iniciando procesamiento async (thread={}, branch={}, periodo {} a {})",
                ev.getRequestId(), Thread.currentThread().getName(),
                ev.getBranch(), ev.getFrom(), ev.getTo());

        try {
            SalesAggregates agg = salesAggregationService.calculateAggregates(
                    ev.getFrom(), ev.getTo(), ev.getBranch());
            log.info("[{}] Agregados calculados: {} ventas, {} unidades", ev.getRequestId(),
                    agg.salesCount(), agg.totalUnits());

            String summary = llmClient.generateSummary(agg);
            log.info("[{}] Resumen generado por LLM ({} chars)", ev.getRequestId(), summary.length());

            emailService.sendWeeklySummary(ev.getEmailTo(), ev.getFrom(), ev.getTo(), summary, agg);
            log.info("[{}] Reporte completado y enviado a {}", ev.getRequestId(), ev.getEmailTo());

        } catch (Exception ex) {
            log.error("[{}] Error procesando reporte async: {}", ev.getRequestId(), ex.getMessage(), ex);
        }
    }
}
