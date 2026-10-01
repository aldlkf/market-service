package com.marketplace.repository;

import com.marketplace.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByTitleContainingIgnoreCaseAndPriceBetween(
            String title,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );
}