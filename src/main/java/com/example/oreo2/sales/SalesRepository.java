package com.example.oreo2.sales;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SalesRepository extends JpaRepository<Sale, String> {

    @Query("""
        SELECT s FROM Sale s
        WHERE s.soldAt BETWEEN :from AND :to
          AND (:branch IS NULL OR s.branch = :branch)
        """)
    Page<Sale> findByFilters(@Param("from") Instant from,
                             @Param("to") Instant to,
                             @Param("branch") String branch,
                             Pageable pageable);

    @Query("""
        SELECT s FROM Sale s
        WHERE s.soldAt BETWEEN :from AND :to
          AND (:branch IS NULL OR s.branch = :branch)
        """)
    List<Sale> findInRange(@Param("from") Instant from,
                           @Param("to") Instant to,
                           @Param("branch") String branch);
}