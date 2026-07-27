package com.marketplace;

import com.marketplace.model.User;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import com.marketplace.service.OrderService;
import com.marketplace.service.ProductService;

import java.math.BigDecimal;

public class Application {

    public static void main(String[] args){
        System.out.println("=== MARKETPLACE IS RUNNING NOW ===");

        ProductRepository productRepository = new ProductRepository();
        UserRepository userRepository = new UserRepository();

        ProductService productService = new ProductService(productRepository);
        OrderService orderService = new OrderService(userRepository, productRepository);

        productService.createProduct(1l,"RTX 5070", new BigDecimal(350000.00), 3);

        User ilyas = new User(1L,"Ilyas","ilyas@example.com", new BigDecimal(500000.00));
        userRepository.save(ilyas);

        System.out.println("USER "+ilyas+" IS SIGNED UP");

        orderService.createOrder(1L,1L,1);

        orderService.createOrder(1L,1L,1);

    }
}
