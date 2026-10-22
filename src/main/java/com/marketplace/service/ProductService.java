package com.marketplace.service;

import com.marketplace.dto.ProductRequest;
import com.marketplace.exception.ProductNotFoundException;
import com.marketplace.model.Product;
import com.marketplace.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ProductService{

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Page<Product> getProducts(String query, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable){
        BigDecimal min = (minPrice != null) ? minPrice : BigDecimal.ZERO;
        BigDecimal max = (maxPrice != null) ? maxPrice : new BigDecimal("999999999");
        return productRepository.findByTitleContainingIgnoreCaseAndPriceBetween(query, min, max, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));
    }

    @Transactional
    public Product createProduct(ProductRequest request){
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        return productRepository.save(product);
    }

    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public Product updateProduct(Long id, ProductRequest request){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));

        product.setTitle(request.getTitle());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        return productRepository.save(product);
    }

    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id){
        if (!productRepository.existsById(id)){
            throw new ProductNotFoundException("Товар не найден");
        }
        productRepository.deleteById(id);
    }
}