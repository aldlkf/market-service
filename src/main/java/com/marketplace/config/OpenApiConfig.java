package com.marketplace.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Marketplace Service API")
                        .version("1.0.0")
                        .description("RESTful API сервиса маркетплейса с управлением пользователями, товарами и заказами")
                        .contact(new Contact()
                                .name("Ilyassick")
                                .email("kudaibergenov.ilyas11@gmail.com")));
    }
}