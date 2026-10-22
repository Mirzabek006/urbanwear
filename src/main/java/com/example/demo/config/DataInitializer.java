package com.example.demo.config;

import com.example.demo.model.Order;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.BranchService;
import com.example.demo.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(OrderRepository orderRepository,
                                      ProductService productService,
                                      BranchService branchService) {
        return args -> {
            productService.initProductsIfEmpty();
            branchService.initBranchesIfEmpty();

            if (orderRepository.count() == 0) {
                orderRepository.save(new Order("Qishki Pufik Kurtka buyurtmasi", new BigDecimal("750000.00")));
            }
        };
    }
}
