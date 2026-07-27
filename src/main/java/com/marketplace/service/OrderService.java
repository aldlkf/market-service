package com.marketplace.service;

import com.marketplace.model.Product;
import com.marketplace.model.User;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;

import java.math.BigDecimal;

public class OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(UserRepository userRepository, ProductRepository productRepository){
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public boolean createOrder(Long userId, Long productId, int quantity){
        System.out.println(">>> MAKING ORDER <<<");

        var userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()){
            System.out.println("ERROR: NO USER WITH "+userId+" Id");
            return false;
        }
        User user = userOptional.get();

        var productOptional = productRepository.findById(productId);
        if(productOptional.isEmpty()){
            System.out.println("ERROR: NO PRODUCT WITH "+productId+" Id");
            return false;
        }
        Product product = productOptional.get();

        if(product.getStockQuantity()<quantity){
            System.out.println("ERROR: NOT ENOUGH ON STOCK! AVAILABLE: "+product.getStockQuantity());
            return false;
        }

        BigDecimal totalPrice = product.getPrice().multiply(new BigDecimal(quantity));
        System.out.println("TOTAL: "+String.format("%,.2f KZT", totalPrice));

        if(user.getBalance().compareTo(totalPrice)<0){
            System.out.println("ERROR: USER "+user.getName()+" HAS NOT ENOUGH MONEY! BALANCE: "+ user.getFormattedBalance());
            return false;
        }

        BigDecimal newBalance = user.getBalance().subtract(totalPrice);
        user.setBalance(newBalance);

        product.setStockQuantity(product.getStockQuantity() - quantity);

        System.out.println("SUCCESS: THE ORDER IS FOR "+user.getName());
        System.out.println("BALANCE NOW: "+user.getFormattedBalance());
        System.out.println("IN STOCK NOW: "+product.getStockQuantity());

        return true;
    }

}
