package com.practice.priceservice.config;

import com.practice.priceservice.application.PriceService;
import com.practice.priceservice.domain.PriceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public PriceService priceService(PriceRepository priceRepository) {
        return new PriceService(priceRepository);
    }
}
