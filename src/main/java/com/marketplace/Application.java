package com.marketplace;

import com.marketplace.model.User;
import com.marketplace.repository.JdbcUserRepository;
import com.marketplace.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;

public class Application {

    public static void main(String[] args) {
        UserRepository userRepository = new JdbcUserRepository();

        System.out.println("=== Достаем пользователя из PostgreSQL ===");
        Optional<User> userOptional = userRepository.findById(1L);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println("Найден пользователь: " + user.getName() + " | Баланс: " + user.getBalance());

            System.out.println("\n=== Обновляем баланс в БД ===");
            user.setBalance(user.getBalance().subtract(new BigDecimal("50000.00")));
            userRepository.save(user);
            System.out.println("Баланс успешно обновлен!");

            Optional<User> updatedUser = userRepository.findById(1L);
            updatedUser.ifPresent(u ->
                    System.out.println("Проверка из БД -> Новый баланс: " + u.getBalance())
            );
        } else {
            System.out.println("Пользователь с ID = 1 не найден в базе данных.");
        }
    }
}