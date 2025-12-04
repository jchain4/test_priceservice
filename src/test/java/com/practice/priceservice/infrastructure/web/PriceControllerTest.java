package com.practice.priceservice.infrastructure.web;

import com.practice.priceservice.application.PriceService;
import com.practice.priceservice.domain.Price;
import com.practice.priceservice.infrastructure.web.dto.PriceResponse;
import com.practice.priceservice.infrastructure.web.mapper.PriceMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitarios para PriceController usando @WebMvcTest.
 * Solo se prueba la capa de controller, mockeando las dependencias.
 */
@WebMvcTest(PriceController.class)
@DisplayName("Unit Tests - PriceController")
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PriceService priceService;

    @MockitoBean
    private PriceMapper priceMapper;

    @Test
    @DisplayName("Cuando se encuentra precio aplicable, debe retornar 200 OK con los datos del precio")
    void givenPriceExists_whenGetPrice_thenReturnsOkWithPriceData() throws Exception {
        // Given
        LocalDateTime testDate = LocalDateTime.of(2024, 6, 14, 10, 0, 0);
        Integer productId = 35455;
        Integer brandId = 1;

        Price priceEntity = new Price(
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

        PriceResponse priceResponse = new PriceResponse(
                productId,
                brandId,
                1,
                LocalDateTime.of(2024, 6, 14, 0, 0, 0),
                LocalDateTime.of(2024, 12, 31, 23, 59, 59),
                35.50,
                "EUR"
        );

        when(priceService.getBestPrice(testDate, productId, brandId))
                .thenReturn(Optional.of(priceEntity));
        when(priceMapper.toResponse(priceEntity))
                .thenReturn(priceResponse);

        // When & Then
        mockMvc.perform(get("/api/prices")
                        .param("date", "2024-06-14T10:00:00")
                        .param("productId", String.valueOf(productId))
                        .param("brandId", String.valueOf(brandId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.brandId").value(brandId))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.currency").value("EUR"));

        verify(priceService, times(1)).getBestPrice(testDate, productId, brandId);
        verify(priceMapper, times(1)).toResponse(priceEntity);
    }

    @Test
    @DisplayName("Cuando no se encuentra precio aplicable, debe retornar 404 Not Found")
    void givenPriceNotFound_whenGetPrice_thenReturnsNotFound() throws Exception {
        // Given
        LocalDateTime testDate = LocalDateTime.of(2024, 6, 14, 10, 0, 0);
        Integer productId = 99999;
        Integer brandId = 1;

        when(priceService.getBestPrice(testDate, productId, brandId))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/prices")
                        .param("date", "2024-06-14T10:00:00")
                        .param("productId", String.valueOf(productId))
                        .param("brandId", String.valueOf(brandId)))
                .andExpect(status().isNotFound());

        verify(priceService, times(1)).getBestPrice(testDate, productId, brandId);
        verifyNoInteractions(priceMapper);
    }

    @Test
    @DisplayName("Cuando falta el parámetro date, debe retornar 400 Bad Request")
    void givenMissingDateParameter_whenGetPrice_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(priceService);
        verifyNoInteractions(priceMapper);
    }

    @Test
    @DisplayName("Cuando falta el parámetro productId, debe retornar 400 Bad Request")
    void givenMissingProductIdParameter_whenGetPrice_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", "2024-06-14T10:00:00")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(priceService);
        verifyNoInteractions(priceMapper);
    }

    @Test
    @DisplayName("Cuando falta el parámetro brandId, debe retornar 400 Bad Request")
    void givenMissingBrandIdParameter_whenGetPrice_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", "2024-06-14T10:00:00")
                        .param("productId", "35455"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(priceService);
        verifyNoInteractions(priceMapper);
    }

    @Test
    @DisplayName("Cuando el formato de fecha es inválido, debe retornar 400 Bad Request")
    void givenInvalidDateFormat_whenGetPrice_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", "invalid-date-format")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(priceService);
        verifyNoInteractions(priceMapper);
    }

    @Test
    @DisplayName("Cuando productId no es un número positivo, debe retornar 400 Bad Request")
    void givenInvalidProductId_whenGetPrice_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", "2024-06-14T10:00:00")
                        .param("productId", "-1")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(priceService);
        verifyNoInteractions(priceMapper);
    }

    @Test
    @DisplayName("Cuando brandId no es un número positivo, debe retornar 400 Bad Request")
    void givenInvalidBrandId_whenGetPrice_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", "2024-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(priceService);
        verifyNoInteractions(priceMapper);
    }

    @Test
    @DisplayName("Debe llamar al mapper solo cuando el service devuelve resultado")
    void givenServiceReturnsPrice_whenGetPrice_thenCallsMapperOnce() throws Exception {
        // Given
        LocalDateTime testDate = LocalDateTime.of(2024, 6, 14, 16, 0, 0);
        Integer productId = 35455;
        Integer brandId = 1;

        Price priceEntity = new Price(
                2L,
                brandId,
                LocalDateTime.of(2024, 6, 14, 15, 0, 0),
                LocalDateTime.of(2024, 6, 14, 18, 30, 0),
                2,
                productId,
                1,
                25.45,
                "EUR"
        );

        PriceResponse priceResponse = new PriceResponse(
                productId,
                brandId,
                2,
                LocalDateTime.of(2024, 6, 14, 15, 0, 0),
                LocalDateTime.of(2024, 6, 14, 18, 30, 0),
                25.45,
                "EUR"
        );

        when(priceService.getBestPrice(testDate, productId, brandId))
                .thenReturn(Optional.of(priceEntity));
        when(priceMapper.toResponse(priceEntity))
                .thenReturn(priceResponse);

        // When
        mockMvc.perform(get("/api/prices")
                        .param("date", "2024-06-14T16:00:00")
                        .param("productId", String.valueOf(productId))
                        .param("brandId", String.valueOf(brandId)))
                .andExpect(status().isOk());

        // Then
        verify(priceService, times(1)).getBestPrice(testDate, productId, brandId);
        verify(priceMapper, times(1)).toResponse(priceEntity);
        verifyNoMoreInteractions(priceService);
        verifyNoMoreInteractions(priceMapper);
    }
}
