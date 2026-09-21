package com.marketplace;

import com.marketplace.model.User;
import com.marketplace.repository.UserRepository;
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
    public CommandLineRunner runner(UserRepository userRepository) {
        return args -> {
            System.out.println("\n==========================================");
            System.out.println("=== Проверка Spring Data JPA и Hibernate ===");

            userRepository.findById(1L).ifPresent(user -> {
                System.out.println("Найден пользователь из БД: " + user.getName() + " | Баланс: " + user.getBalance());

                user.setBalance(user.getBalance().subtract(new BigDecimal("10000.00")));
                userRepository.save(user);
                System.out.println("Баланс обновлен через Spring Data JPA!");
            });

            userRepository.findByEmail("ilyassick@example.com").ifPresent(user -> {
                System.out.println("Проверка findByEmail -> Имя: " + user.getName() + " | Новый баланс: " + user.getBalance());
            });

            System.out.println("==========================================\n");
        };
    }
}