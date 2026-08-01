package com.marketplace;

import com.marketplace.model.Order;
import com.marketplace.model.User;
import com.marketplace.repository.OrderRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import com.marketplace.service.OrderService;
import com.marketplace.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

public class Application {

    public static void main(String[] args){
        System.out.println("=== MARKETPLACE IS RUNNING NOW ===");

        ProductRepository productRepository = new ProductRepository();
        UserRepository userRepository = new UserRepository();
        OrderRepository orderRepository = new OrderRepository();

        ProductService productService = new ProductService(productRepository);
        OrderService orderService = new OrderService(userRepository, productRepository, orderRepository);

        productService.createProduct(1L,"RTX 5070", new BigDecimal(350000.00), 3);
        productService.createProduct(2L,"R7 7800x3D", new BigDecimal(150000.00), 10);

        User ilyas = new User(1L,"Ilyas","ilyas@example.com", new BigDecimal(2600000.00));
        userRepository.save(ilyas);

        orderService.createOrder(1L,1L,1);
        orderService.createOrder(1L,2L,1);

        System.out.println("\n=== ORDER HISTORY " + ilyas.getName().toUpperCase()+ " ===");
        List<Order> history = orderService.getUserOrders(1L);
        for (Order order : history) {
            System.out.println(order);
        }

        System.out.println("\nBALANCE NOW: " + ilyas.getFormattedBalance());
    }
}
