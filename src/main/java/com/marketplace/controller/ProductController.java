package com.marketplace.controller;

import com.marketplace.dto.ProductRequest;
import com.marketplace.model.Product;
import com.marketplace.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.aspectj.bridge.IMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Управление каталогом товаров")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    @Operation(
            summary = "Каталог товаров с фильтрацией и пагинацией",
            description = "Позволяет искать товары по названию, диапазону цен и сортировать результаты. Доступно всем без авторизации."
    )
    public Page<Product> getProducts(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        BigDecimal min = (minPrice != null) ? minPrice : BigDecimal.ZERO;
        BigDecimal max = (maxPrice != null) ? maxPrice : new BigDecimal("999999999");

        return productRepository.findByTitleContainingIgnoreCaseAndPriceBetween(
                query, min, max, pageable
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар по ID", description = "Возвращает детальную информацию о товаре. Доступно всем без авторизации.")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Создать новый товар (ADMIN)", description = "Добавляет товар в базу. Требуется роль ADMIN.")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request){
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        Product saved = productRepository.save(product);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить товар (ADMIN)", description = "Обновляет данные существующего товара. Требуется роль ADMIN.")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ){
        return productRepository.findById(id)
                .map(product ->{
                    product.setTitle(request.getTitle());
                    product.setPrice(request.getPrice());
                    product.setStockQuantity(request.getStockQuantity());
                    return ResponseEntity.ok(productRepository.save(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить товар (ADMIN)", description = "Удаляет товар из каталога. Требуется роль ADMIN.")
    public ResponseEntity<Void> deletePost(@PathVariable Long id){
        if (!productRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}