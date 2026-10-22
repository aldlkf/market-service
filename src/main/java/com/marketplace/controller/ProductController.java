package com.marketplace.controller;

import com.marketplace.dto.ProductRequest;
import com.marketplace.model.Product;
import com.marketplace.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Управление каталогом товаров")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
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
        return productService.getProducts(query, minPrice, maxPrice, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар по ID", description = "Возвращает детальную информацию о товаре. Результат кешируется.")
    public ResponseEntity<Product> getProductById(@PathVariable Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    @Operation(summary = "Создать новый товар (ADMIN)", description = "Добавляет товар в базу. Требуется роль ADMIN.")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request){
        Product saved = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить товар (ADMIN)", description = "Обновляет данные существующего товара и сбрасывает кеш. Требуется роль ADMIN.")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ){
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить товар (ADMIN)", description = "Помечает товар как удаленный (Soft Delete) и сбрасывает кеш. Требуется роль ADMIN.")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}