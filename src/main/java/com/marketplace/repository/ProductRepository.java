package com.marketplace.repository;

import com.marketplace.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository {

    private final List<Product> products = new ArrayList<>();

    public Product save(Product product){
        products.add(product);
        return product;
    }

    public List<Product> findAll(){
        return products;
    }

    public Optional<Product> findById(Long id){
        for (Product product : products){
            if (product.getId().equals(id)){
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }

}
