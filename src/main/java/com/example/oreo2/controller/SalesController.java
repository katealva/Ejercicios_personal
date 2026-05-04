package com.example.oreo2.controller;

import com.example.oreo2.dto.SaleRequest;
import com.example.oreo2.dto.SaleResponse;
import com.example.oreo2.service.SalesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SaleResponse create(@Valid @RequestBody SaleRequest request) {
        return salesService.create(request);
    }

    @GetMapping("/{id}")
    public SaleResponse getById(@PathVariable String id) {
        return salesService.getById(id);
    }

    @GetMapping
    public Page<SaleResponse> list(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) String branch,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return salesService.list(from, to, branch, page, size);
    }

    @PutMapping("/{id}")
    public SaleResponse update(@PathVariable String id,
                               @Valid @RequestBody SaleRequest request) {
        return salesService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        salesService.delete(id);
    }
}
