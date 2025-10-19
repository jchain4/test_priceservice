package com.practice.priceservice.infrastructure.web;

import com.practice.priceservice.application.PriceService;
import com.practice.priceservice.infrastructure.web.mapper.PriceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.mock;

@WebMvcTest(PriceController.class)
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public PriceService priceService() {
            return mock(PriceService.class);
        }

        @Bean
        public PriceMapper priceMapper() {
            return mock(PriceMapper.class);
        }
    }

    @Autowired
    private PriceService priceService;

    @Autowired
    private PriceMapper priceMapper;

    @Test
    void shouldReturnPriceResponse() throws Exception {
        // mismo contenido que antes
    }
}

