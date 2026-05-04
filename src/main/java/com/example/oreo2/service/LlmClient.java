package com.example.oreo2.service;

import com.example.oreo2.dto.SalesAggregates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class LlmClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.llm.url}")
    private String llmUrl;

    @Value("${app.llm.github-token}")
    private String githubToken;

    @Value("${app.llm.model-id}")
    private String modelId;

    public String generateSummary(SalesAggregates agg) {
        String userPrompt = buildUserPrompt(agg);

        Map<String, Object> body = Map.of(
                "model", modelId,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", "Eres un analista que escribe resumenes breves y claros para emails corporativos. Responde siempre en espanol."
                        ),
                        Map.of(
                                "role", "user",
                                "content", userPrompt
                        )
                ),
                "max_tokens", 200
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    llmUrl, HttpMethod.POST, entity, Map.class);

            return extractContent(response.getBody());
        } catch (RestClientException ex) {
            log.error("Error llamando al LLM en {}: {}", llmUrl, ex.getMessage());
            throw new RuntimeException("LLM_UNAVAILABLE", ex);
        }
    }

    private String buildUserPrompt(SalesAggregates agg) {
        return """
                Con estos datos de ventas Oreo del periodo %s a %s: \
                totalUnits=%d, totalRevenue=%s, topSku=%s, topBranch=%s, salesCount=%d. \
                Devuelve un resumen <=120 palabras para enviar por email. Menciona unidades totales, \
                SKU top, sucursal top y total recaudado. Sin alucinar datos.\
                """.formatted(
                        agg.from(), agg.to(),
                        agg.totalUnits(),
                        agg.totalRevenue() != null ? agg.totalRevenue().toPlainString() : "0",
                        agg.topSku() != null ? agg.topSku() : "(sin datos)",
                        agg.topBranch() != null ? agg.topBranch() : "(sin datos)",
                        agg.salesCount()
                );
    }

    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> body) {
        if (body == null) throw new RuntimeException("LLM_EMPTY_RESPONSE");
        List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
        if (choices == null || choices.isEmpty()) throw new RuntimeException("LLM_NO_CHOICES");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        if (message == null) throw new RuntimeException("LLM_NO_MESSAGE");
        Object content = message.get("content");
        return content != null ? content.toString() : "";
    }
}
