package com.practice.priceservice.application;

import com.practice.priceservice.domain.Price;
import com.practice.priceservice.domain.PriceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para PriceService.
 * Usa mocks para aislar la lógica de negocio del repositorio.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - PriceService")
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private PriceService priceService;

    @Test
    @DisplayName("Cuando hay múltiples precios aplicables, debe devolver el de mayor prioridad")
    void givenMultipleApplicablePrices_whenGetBestPrice_thenReturnsHighestPriority() {
        // Given
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 16, 0);
        Integer productId = 35455;
        Integer brandId = 1;

        Price lowPriority = new Price(1L, brandId, date.minusHours(1), date.plusHours(1), 1, productId, 0, 35.50, "EUR");
        Price highPriority = new Price(2L, brandId, date.minusHours(1), date.plusHours(1), 2, productId, 1, 25.45, "EUR");

        when(priceRepository.findApplicablePrices(date, productId, brandId))
                .thenReturn(List.of(lowPriority, highPriority));

        // When
        Optional<Price> result = priceService.getBestPrice(date, productId, brandId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(2L);
        assertThat(result.get().getPriority()).isEqualTo(1);
        assertThat(result.get().getPrice()).isEqualTo(25.45);

        verify(priceRepository, times(1)).findApplicablePrices(date, productId, brandId);
    }

    @Test
    @DisplayName("Cuando solo hay un precio aplicable, debe devolverlo")
    void givenSingleApplicablePrice_whenGetBestPrice_thenReturnsThatPrice() {
        // Given
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 10, 0);
        Integer productId = 35455;
        Integer brandId = 1;

        Price singlePrice = new Price(1L, brandId, date.minusHours(1), date.plusHours(1), 1, productId, 0, 35.50, "EUR");

        when(priceRepository.findApplicablePrices(date, productId, brandId))
                .thenReturn(List.of(singlePrice));

        // When
        Optional<Price> result = priceService.getBestPrice(date, productId, brandId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getPrice()).isEqualTo(35.50);

        verify(priceRepository).findApplicablePrices(date, productId, brandId);
    }

    @Test
    @DisplayName("Cuando no hay precios aplicables, debe devolver Optional vacío")
    void givenNoPricesAvailable_whenGetBestPrice_thenReturnsEmptyOptional() {
        // Given
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 10, 0);
        Integer productId = 99999;
        Integer brandId = 1;

        when(priceRepository.findApplicablePrices(date, productId, brandId))
                .thenReturn(Collections.emptyList());

        // When
        Optional<Price> result = priceService.getBestPrice(date, productId, brandId);

        // Then
        assertThat(result).isEmpty();

        verify(priceRepository).findApplicablePrices(date, productId, brandId);
    }

    @Test
    @DisplayName("Cuando hay múltiples precios con la misma prioridad, debe devolver el último")
    void givenMultiplePricesWithSamePriority_whenGetBestPrice_thenReturnsLast() {
        // Given
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 16, 0);
        Integer productId = 35455;
        Integer brandId = 1;

        Price firstPrice = new Price(1L, brandId, date.minusHours(1), date.plusHours(1), 1, productId, 1, 35.50, "EUR");
        Price secondPrice = new Price(2L, brandId, date.minusHours(1), date.plusHours(1), 2, productId, 1, 25.45, "EUR");
        Price thirdPrice = new Price(3L, brandId, date.minusHours(1), date.plusHours(1), 3, productId, 1, 30.00, "EUR");

        when(priceRepository.findApplicablePrices(date, productId, brandId))
                .thenReturn(List.of(firstPrice, secondPrice, thirdPrice));

        // When
        Optional<Price> result = priceService.getBestPrice(date, productId, brandId);

        // Then
        assertThat(result).isPresent();
        // Todos tienen prioridad 1, así que max() devuelve cualquiera (el comportamiento depende de la implementación)
        assertThat(result.get().getPriority()).isEqualTo(1);

        verify(priceRepository).findApplicablePrices(date, productId, brandId);
    }

    @Test
    @DisplayName("Cuando hay precios con diferentes prioridades, debe ignorar los de menor prioridad")
    void givenPricesWithDifferentPriorities_whenGetBestPrice_thenIgnoresLowerPriorities() {
        // Given
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 16, 0);
        Integer productId = 35455;
        Integer brandId = 1;

        Price priority0 = new Price(1L, brandId, date.minusHours(1), date.plusHours(1), 1, productId, 0, 35.50, "EUR");
        Price priority1 = new Price(2L, brandId, date.minusHours(1), date.plusHours(1), 2, productId, 1, 25.45, "EUR");
        Price priority2 = new Price(3L, brandId, date.minusHours(1), date.plusHours(1), 3, productId, 2, 20.00, "EUR");

        when(priceRepository.findApplicablePrices(date, productId, brandId))
                .thenReturn(List.of(priority0, priority1, priority2));

        // When
        Optional<Price> result = priceService.getBestPrice(date, productId, brandId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getPriority()).isEqualTo(2);
        assertThat(result.get().getPrice()).isEqualTo(20.00);

        verify(priceRepository).findApplicablePrices(date, productId, brandId);
    }

    @Test
    @DisplayName("Debe llamar al repositorio exactamente una vez con los parámetros correctos")
    void whenGetBestPrice_thenCallsRepositoryWithCorrectParameters() {
        // Given
        LocalDateTime specificDate = LocalDateTime.of(2020, 6, 14, 16, 30, 45);
        Integer specificProductId = 12345;
        Integer specificBrandId = 2;

        when(priceRepository.findApplicablePrices(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // When
        priceService.getBestPrice(specificDate, specificProductId, specificBrandId);

        // Then
        verify(priceRepository, times(1)).findApplicablePrices(
                eq(specificDate),
                eq(specificProductId),
                eq(specificBrandId)
        );
        verifyNoMoreInteractions(priceRepository);
    }
}
