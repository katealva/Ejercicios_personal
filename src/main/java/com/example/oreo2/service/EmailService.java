package com.example.oreo2.service;

import com.example.oreo2.dto.SalesAggregates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public void sendWeeklySummary(String to, LocalDate from, LocalDate toDate,
                                  String summaryText, SalesAggregates agg) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject("Reporte Semanal Oreo - " + from + " a " + toDate);
        message.setText(buildBody(summaryText, agg));

        try {
            mailSender.send(message);
            log.info("Email enviado a {} para periodo {} a {}", to, from, toDate);
        } catch (MailException ex) {
            log.error("Error enviando email a {}: {}", to, ex.getMessage());
            throw new RuntimeException("MAIL_UNAVAILABLE", ex);
        }
    }

    private String buildBody(String summaryText, SalesAggregates agg) {
        return """
                %s

                ----------------------------------------
                Metricas del periodo %s a %s:
                  Total unidades : %d
                  Total recaudado: %s
                  SKU top        : %s
                  Sucursal top   : %s
                  Ventas         : %d
                ----------------------------------------

                Reporte generado automaticamente por Oreo Insight Factory.
                """.formatted(
                        summaryText,
                        agg.from(), agg.to(),
                        agg.totalUnits(),
                        agg.totalRevenue() != null ? agg.totalRevenue().toPlainString() : "0",
                        agg.topSku() != null ? agg.topSku() : "(sin datos)",
                        agg.topBranch() != null ? agg.topBranch() : "(sin datos)",
                        agg.salesCount()
                );
    }
}
