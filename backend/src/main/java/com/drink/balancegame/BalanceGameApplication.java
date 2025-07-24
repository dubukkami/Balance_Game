package com.drink.balancegame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.drink.balancegame.repository")
public class BalanceGameApplication {
    public static void main(String[] args) {
        SpringApplication.run(BalanceGameApplication.class, args);
    }
}