package com.example.oreo2.service;

import com.example.oreo2.dto.SalesAggregates;
import com.example.oreo2.entity.Sale;
import com.example.oreo2.repository.SalesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesAggregationService {

    private final SalesRepository salesRepository;

    public SalesAggregates calculateAggregates(LocalDate from, LocalDate to, String branch) {
        Instant fromInstant = from.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant toInstant = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().minusNanos(1);

        List<Sale> sales = salesRepository.findInRange(fromInstant, toInstant, branch);

        if (sales.isEmpty()) {
            return SalesAggregates.empty(from, to);
        }

        int totalUnits = sales.stream().mapToInt(Sale::getUnits).sum();

        BigDecimal totalRevenue = sales.stream()
                .map(s -> s.getPrice().multiply(BigDecimal.valueOf(s.getUnits())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String topSku = topByUnits(sales, Sale::getSku);
        String topBranch = topByUnits(sales, Sale::getBranch);

        return new SalesAggregates(totalUnits, totalRevenue, topSku, topBranch, sales.size(), from, to);
    }

    private String topByUnits(List<Sale> sales, java.util.function.Function<Sale, String> classifier) {
        Map<String, Integer> grouped = sales.stream()
                .collect(Collectors.groupingBy(classifier, Collectors.summingInt(Sale::getUnits)));

        return grouped.entrySet().stream()
                .max(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue)
                        .thenComparing(Map.Entry::getKey, Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
