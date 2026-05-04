package com.example.oreo2.service;

import com.example.oreo2.common.ForbiddenException;
import com.example.oreo2.common.NotFoundException;
import com.example.oreo2.dto.SaleRequest;
import com.example.oreo2.dto.SaleResponse;
import com.example.oreo2.entity.Role;
import com.example.oreo2.entity.Sale;
import com.example.oreo2.entity.User;
import com.example.oreo2.repository.SalesRepository;
import com.example.oreo2.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesRepository salesRepository;
    private final CurrentUserProvider currentUserProvider;

    public SaleResponse create(SaleRequest req) {
        User actor = currentUserProvider.getCurrentUser();
        if (actor.getRole() == Role.BRANCH
                && !actor.getBranch().equalsIgnoreCase(req.branch()))
            throw new ForbiddenException("BRANCH users can only create sales for their own branch");

        Sale sale = Sale.builder()
                .sku(req.sku()).units(req.units()).price(req.price())
                .branch(req.branch()).soldAt(req.soldAt())
                .createdBy(actor.getUsername()).build();
        return SaleResponse.from(salesRepository.save(sale));
    }

    public SaleResponse getById(String id) {
        User actor = currentUserProvider.getCurrentUser();
        Sale sale = salesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sale not found: " + id));
        if (actor.getRole() == Role.BRANCH
                && !actor.getBranch().equalsIgnoreCase(sale.getBranch()))
            throw new ForbiddenException("BRANCH users can only view their own branch sales");
        return SaleResponse.from(sale);
    }

    public Page<SaleResponse> list(Instant from, Instant to, String branch, int page, int size) {
        User actor = currentUserProvider.getCurrentUser();
        if (actor.getRole() == Role.BRANCH) branch = actor.getBranch();
        if (from == null) from = Instant.now().minusSeconds(60L * 60 * 24 * 30);
        if (to == null)   to   = Instant.now();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "soldAt"));
        return salesRepository.findByFilters(from, to, branch, pageable).map(SaleResponse::from);
    }

    public SaleResponse update(String id, SaleRequest req) {
        User actor = currentUserProvider.getCurrentUser();
        Sale sale = salesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sale not found: " + id));
        if (actor.getRole() == Role.BRANCH
                && !actor.getBranch().equalsIgnoreCase(sale.getBranch()))
            throw new ForbiddenException("BRANCH users can only update their own branch sales");
        if (actor.getRole() == Role.BRANCH
                && !actor.getBranch().equalsIgnoreCase(req.branch()))
            throw new ForbiddenException("BRANCH users cannot reassign a sale to another branch");

        sale.setSku(req.sku()); sale.setUnits(req.units()); sale.setPrice(req.price());
        sale.setBranch(req.branch()); sale.setSoldAt(req.soldAt());
        return SaleResponse.from(salesRepository.save(sale));
    }

    public void delete(String id) {
        User actor = currentUserProvider.getCurrentUser();
        if (actor.getRole() == Role.BRANCH)
            throw new ForbiddenException("Only CENTRAL users can delete sales");
        if (!salesRepository.existsById(id))
            throw new NotFoundException("Sale not found: " + id);
        salesRepository.deleteById(id);
    }
}
