package com.practice.priceservice.infrastructure.web;

import com.practice.priceservice.application.PriceService;
import com.practice.priceservice.infrastructure.web.exception.GlobalExceptionHandler;
import com.practice.priceservice.infrastructure.web.mapper.PriceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

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

        @Bean
        public GlobalExceptionHandler globalExceptionHandler() {
            return new GlobalExceptionHandler();
        }
    }

    @Autowired
    private PriceService priceService;

    @Autowired
    private PriceMapper priceMapper;

    @Test
    void shouldReturnPriceWhenFound() throws Exception {
        LocalDateTime testDate = LocalDateTime.of(2024, 6, 14, 10, 0, 0);
        Integer productId = 35455;
        Integer brandId = 1;

        var priceEntity = new com.practice.priceservice.domain.Price(
                1L,
                brandId,
                LocalDateTime.of(2024, 6, 14, 0, 0, 0),
                LocalDateTime.of(2024, 12, 31, 23, 59, 59),
                1,
                productId,
                0,
                35.50,
                "EUR"
        );

        var priceResponse = new com.practice.priceservice.infrastructure.web.dto.PriceResponse(
                productId,
                brandId,
                1,
                LocalDateTime.of(2024, 6, 14, 0, 0, 0),
                LocalDateTime.of(2024, 12, 31, 23, 59, 59),
                35.50,
                "EUR"
        );

        org.mockito.Mockito.when(priceService.getBestPrice(testDate, productId, brandId))
                .thenReturn(java.util.Optional.of(priceEntity));
        org.mockito.Mockito.when(priceMapper.toResponse(priceEntity))
                .thenReturn(priceResponse);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/prices")
                        .param("date", "2024-06-14T10:00:00")
                        .param("productId", String.valueOf(productId))
                        .param("brandId", String.valueOf(brandId)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.productId").value(productId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.brandId").value(brandId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.priceList").value(1))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.price").value(35.50))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.currency").value("EUR"));

        org.mockito.Mockito.verify(priceService).getBestPrice(testDate, productId, brandId);
        org.mockito.Mockito.verify(priceMapper).toResponse(priceEntity);
    }

    @Test
    void shouldReturnNotFoundWhenPriceNotFound() throws Exception {
        LocalDateTime testDate = LocalDateTime.of(2024, 6, 14, 10, 0, 0);
        Integer productId = 99999;
        Integer brandId = 1;

        org.mockito.Mockito.when(priceService.getBestPrice(testDate, productId, brandId))
                .thenReturn(java.util.Optional.empty());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/prices")
                        .param("date", "2024-06-14T10:00:00")
                        .param("productId", String.valueOf(productId))
                        .param("brandId", String.valueOf(brandId)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound());

        org.mockito.Mockito.verify(priceService).getBestPrice(testDate, productId, brandId);
    }

    @Test
    void shouldReturnBadRequestWhenDateParameterIsMissing() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/prices")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenProductIdParameterIsMissing() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/prices")
                        .param("date", "2024-06-14T10:00:00")
                        .param("brandId", "1"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenBrandIdParameterIsMissing() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/prices")
                        .param("date", "2024-06-14T10:00:00")
                        .param("productId", "35455"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenDateFormatIsInvalid() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/prices")
                        .param("date", "invalid-date")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest());
    }
}

