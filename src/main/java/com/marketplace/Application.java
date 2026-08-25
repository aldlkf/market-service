package com.marketplace;

import com.marketplace.exception.MarketplaceException;
import com.marketplace.model.User;
import com.marketplace.repository.OrderRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import com.marketplace.service.OrderService;
import com.marketplace.service.ProductService;

import java.math.BigDecimal;

public class Application {

    public static void main(String[] args){
        ProductRepository productRepository = new ProductRepository();
        UserRepository userRepository = new UserRepository();
        OrderRepository orderRepository = new OrderRepository();

        ProductService productService = new ProductService(productRepository);
        OrderService orderService = new OrderService(userRepository, productRepository, orderRepository);

        productService.createProduct(1L, "RTX 5070", new BigDecimal("350000.00"), 5);
        User ilyas = new User(1L,"Ilyas","ilyas@example.com", new BigDecimal("500000.00"));
        userRepository.save(ilyas);

        try {
            System.out.println("Trying to buy the product...");
            orderService.createOrder(3L,1L,1);
        } catch (MarketplaceException e){
            System.out.println("CAUGHT EXCEPTION: " + e.getMessage());
        }
    }
}
