package com.marketplace;

import com.marketplace.model.Order;
import com.marketplace.service.OrderService;
import org.aspectj.weaver.ast.Or;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.math.BigDecimal;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner runner(OrderService orderService) {
        return args -> {
            System.out.println("\n==========================================");
            System.out.println("=== Тестирование покупки через OrderService ===");

            Order createOrder = orderService.createOrder(1L, 1L,1);

            System.out.println("Заказ успешно создан");
            System.out.println("ID заказа: " + createOrder.getId());
            System.out.println("Покупатель: " + createOrder.getUser().getName());
            System.out.println("Товар: " + createOrder.getProduct().getTitle());
            System.out.println("Итоговая сумма: " + createOrder.getTotalPrice());
            System.out.println("Статус: " + createOrder.getStatus());
            System.out.println("==========================================\n");
        };
    }
}