package com.marketplace.service;

import com.marketplace.model.Product;
import com.marketplace.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Long id, String title, BigDecimal price, int quantity){
        if (price.compareTo(BigDecimal.ZERO)<0){
            throw new IllegalArgumentException("THE PRICE CAN'T BE NEGATIVE");
        }

        Product product = new Product(id, title, price, quantity);
        return productRepository.save(product);
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public boolean buyProduct(Long productId, int amountToBuy){
        var optionalProduct = productRepository.findById(productId);

        if(optionalProduct.isEmpty()){
            System.out.println("ERROR: PRODUCT WITH ID "+productId+"NOT FOUND");
            return false;
        }

        Product product = optionalProduct.get();

        if(product.getStockQuantity()<amountToBuy){
            System.out.println("ERROR: NOT ENOUGH ON STOCK! AVAILABLE: "+product.getStockQuantity());
            return false;
        }

        int newQuantity = product.getStockQuantity() - amountToBuy;
        product.setStockQuantity(newQuantity);

        System.out.println("SUCCESSFULLY PURCHASED "+amountToBuy+" OF "+product.getTitle());
        return true;
    }
}
