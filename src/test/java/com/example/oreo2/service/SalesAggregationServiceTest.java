package com.example.oreo2.service;

import com.example.oreo2.dto.SalesAggregates;
import com.example.oreo2.entity.Sale;
import com.example.oreo2.repository.SalesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalesAggregationServiceTest {

    @Mock
    private SalesRepository salesRepository;

    @InjectMocks
    private SalesAggregationService salesAggregationService;

    private Sale createSale(String sku, int units, String price, String branch) {
        return Sale.builder()
                .id("s_" + sku + "_" + units)
                .sku(sku)
                .units(units)
                .price(new BigDecimal(price))
                .branch(branch)
                .soldAt(Instant.parse("2025-09-03T10:00:00Z"))
                .createdBy("tester")
                .build();
    }

    @Test
    void shouldCalculateCorrectAggregatesWithValidData() {
        List<Sale> mockSales = List.of(
                createSale("OREO_CLASSIC", 10, "1.99", "Miraflores"),
                createSale("OREO_DOUBLE", 5, "2.49", "San Isidro"),
                createSale("OREO_CLASSIC", 15, "1.99", "Miraflores")
        );
        when(salesRepository.findInRange(any(), any(), any())).thenReturn(mockSales);

        SalesAggregates result = salesAggregationService.calculateAggregates(
                LocalDate.of(2025, 9, 1), LocalDate.of(2025, 9, 7), null);

        assertThat(result.totalUnits()).isEqualTo(30);
        assertThat(result.totalRevenue()).isEqualByComparingTo(new BigDecimal("62.20"));
        assertThat(result.topSku()).isEqualTo("OREO_CLASSIC");
        assertThat(result.topBranch()).isEqualTo("Miraflores");
        assertThat(result.salesCount()).isEqualTo(3);
    }

    @Test
    void shouldReturnEmptyAggregatesWhenNoSales() {
        when(salesRepository.findInRange(any(), any(), any())).thenReturn(List.of());

        SalesAggregates result = salesAggregationService.calculateAggregates(
                LocalDate.of(2025, 9, 1), LocalDate.of(2025, 9, 7), "Miraflores");

        assertThat(result.totalUnits()).isZero();
        assertThat(result.totalRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.topSku()).isNull();
        assertThat(result.topBranch()).isNull();
        assertThat(result.salesCount()).isZero();
    }

    @Test
    void shouldFilterByBranchWhenBranchSpecified() {
        when(salesRepository.findInRange(any(), any(), any())).thenReturn(List.of(
                createSale("OREO_CLASSIC", 10, "1.99", "Miraflores")
        ));

        salesAggregationService.calculateAggregates(
                LocalDate.of(2025, 9, 1), LocalDate.of(2025, 9, 7), "Miraflores");

        ArgumentCaptor<String> branchCaptor = ArgumentCaptor.forClass(String.class);
        verify(salesRepository).findInRange(any(Instant.class), any(Instant.class), branchCaptor.capture());
        assertThat(branchCaptor.getValue()).isEqualTo("Miraflores");
    }

    @Test
    void shouldRespectDateRangeFilter() {
        when(salesRepository.findInRange(any(), any(), any())).thenReturn(List.of());
        LocalDate from = LocalDate.of(2025, 9, 1);
        LocalDate to = LocalDate.of(2025, 9, 7);

        salesAggregationService.calculateAggregates(from, to, null);

        ArgumentCaptor<Instant> fromCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Instant> toCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(salesRepository).findInRange(fromCaptor.capture(), toCaptor.capture(), eq((String) null));

        Instant expectedFrom = from.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant expectedToUpperBound = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        assertThat(fromCaptor.getValue()).isEqualTo(expectedFrom);
        assertThat(toCaptor.getValue()).isBefore(expectedToUpperBound);
        assertThat(toCaptor.getValue()).isAfter(to.atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    @Test
    void shouldResolveTopSkuOnTiesAlphabetically() {
        List<Sale> mockSales = List.of(
                createSale("OREO_ZETA", 20, "1.00", "Lima"),
                createSale("OREO_ALPHA", 20, "1.00", "Lima")
        );
        when(salesRepository.findInRange(any(), any(), any())).thenReturn(mockSales);

        SalesAggregates result = salesAggregationService.calculateAggregates(
                LocalDate.of(2025, 9, 1), LocalDate.of(2025, 9, 7), null);

        assertThat(result.topSku()).isEqualTo("OREO_ALPHA");
        assertThat(result.totalUnits()).isEqualTo(40);
    }
}
