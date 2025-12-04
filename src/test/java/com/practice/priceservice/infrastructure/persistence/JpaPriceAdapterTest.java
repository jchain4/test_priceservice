package com.practice.priceservice.infrastructure.persistence;

import com.practice.priceservice.domain.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para JpaPriceAdapter.
 * Verifica la conversión correcta entre entidades JPA y objetos de dominio.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - JpaPriceAdapter")
class JpaPriceAdapterTest {

    @Mock
    private JpaPriceRepository jpaPriceRepository;

    @InjectMocks
    private JpaPriceAdapter adapter;

    @Test
    @DisplayName("Debe convertir correctamente entidades JPA a objetos de dominio")
    void givenJpaEntities_whenFindApplicablePrices_thenConvertsToDomainPrices() {
        // Given
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 16, 0);
        Integer productId = 35455;
        Integer brandId = 1;

        JpaPriceEntity entity = new JpaPriceEntity();
        entity.setId(1L);
        entity.setBrandId(brandId);
        entity.setStartDate(date.minusHours(1));
        entity.setEndDate(date.plusHours(1));
        entity.setPriceList(2);
        entity.setProductId(productId);
        entity.setPriority(1);
        entity.setPrice(25.45);
        entity.setCurrency("EUR");

        when(jpaPriceRepository.findApplicablePrices(date, productId, brandId))
                .thenReturn(List.of(entity));

        // When
        List<Price> result = adapter.findApplicablePrices(date, productId, brandId);

        // Then
        assertThat(result).hasSize(1);

        Price price = result.get(0);
        assertThat(price.getId()).isEqualTo(entity.getId());
        assertThat(price.getBrandId()).isEqualTo(entity.getBrandId());
        assertThat(price.getStartDate()).isEqualTo(entity.getStartDate());
        assertThat(price.getEndDate()).isEqualTo(entity.getEndDate());
        assertThat(price.getPriceList()).isEqualTo(entity.getPriceList());
        assertThat(price.getProductId()).isEqualTo(entity.getProductId());
        assertThat(price.getPriority()).isEqualTo(entity.getPriority());
        assertThat(price.getPrice()).isEqualTo(entity.getPrice());
        assertThat(price.getCurrency()).isEqualTo(entity.getCurrency());

        verify(jpaPriceRepository, times(1)).findApplicablePrices(date, productId, brandId);
    }

    @Test
    @DisplayName("Debe convertir múltiples entidades correctamente")
    void givenMultipleJpaEntities_whenFindApplicablePrices_thenConvertsAllToDomain() {
        // Given
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 16, 0);
        Integer productId = 35455;
        Integer brandId = 1;

        JpaPriceEntity entity1 = createEntity(1L, brandId, productId, 1, 0, 35.50);
        JpaPriceEntity entity2 = createEntity(2L, brandId, productId, 2, 1, 25.45);
        JpaPriceEntity entity3 = createEntity(3L, brandId, productId, 3, 1, 30.00);

        when(jpaPriceRepository.findApplicablePrices(date, productId, brandId))
                .thenReturn(List.of(entity1, entity2, entity3));

        // When
        List<Price> result = adapter.findApplicablePrices(date, productId, brandId);

        // Then
        assertThat(result)
                .hasSize(3)
                .extracting(Price::getId)
                .containsExactly(1L, 2L, 3L);

        assertThat(result)
                .extracting(Price::getPrice)
                .containsExactly(35.50, 25.45, 30.00);

        verify(jpaPriceRepository).findApplicablePrices(date, productId, brandId);
    }

    @Test
    @DisplayName("Cuando no hay entidades, debe devolver lista vacía")
    void givenNoEntities_whenFindApplicablePrices_thenReturnsEmptyList() {
        // Given
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 16, 0);
        Integer productId = 99999;
        Integer brandId = 1;

        when(jpaPriceRepository.findApplicablePrices(date, productId, brandId))
                .thenReturn(Collections.emptyList());

        // When
        List<Price> result = adapter.findApplicablePrices(date, productId, brandId);

        // Then
        assertThat(result).isEmpty();

        verify(jpaPriceRepository).findApplicablePrices(date, productId, brandId);
    }

    @Test
    @DisplayName("Debe llamar al repositorio JPA con los parámetros correctos")
    void whenFindApplicablePrices_thenCallsRepositoryWithCorrectParameters() {
        // Given
        LocalDateTime specificDate = LocalDateTime.of(2020, 6, 14, 16, 30, 45);
        Integer specificProductId = 12345;
        Integer specificBrandId = 2;

        when(jpaPriceRepository.findApplicablePrices(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // When
        adapter.findApplicablePrices(specificDate, specificProductId, specificBrandId);

        // Then
        verify(jpaPriceRepository, times(1)).findApplicablePrices(
                eq(specificDate),
                eq(specificProductId),
                eq(specificBrandId)
        );
        verifyNoMoreInteractions(jpaPriceRepository);
    }

    @Test
    @DisplayName("Debe preservar todos los campos durante la conversión")
    void givenEntityWithAllFields_whenConvert_thenAllFieldsArePreserved() {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 12, 31, 23, 59, 59);
        LocalDateTime queryDate = LocalDateTime.of(2020, 6, 14, 16, 0);

        JpaPriceEntity entity = new JpaPriceEntity();
        entity.setId(123L);
        entity.setBrandId(5);
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);
        entity.setPriceList(99);
        entity.setProductId(77777);
        entity.setPriority(3);
        entity.setPrice(99.99);
        entity.setCurrency("USD");

        when(jpaPriceRepository.findApplicablePrices(queryDate, 77777, 5))
                .thenReturn(List.of(entity));

        // When
        List<Price> result = adapter.findApplicablePrices(queryDate, 77777, 5);

        // Then
        assertThat(result).hasSize(1);
        Price price = result.get(0);

        assertThat(price)
                .extracting(
                        Price::getId,
                        Price::getBrandId,
                        Price::getStartDate,
                        Price::getEndDate,
                        Price::getPriceList,
                        Price::getProductId,
                        Price::getPriority,
                        Price::getPrice,
                        Price::getCurrency
                )
                .containsExactly(
                        123L,
                        5,
                        startDate,
                        endDate,
                        99,
                        77777,
                        3,
                        99.99,
                        "USD"
                );

        verify(jpaPriceRepository).findApplicablePrices(queryDate, 77777, 5);
    }

    // Helper method para crear entidades de prueba
    private JpaPriceEntity createEntity(Long id, Integer brandId, Integer productId,
                                       Integer priceList, Integer priority, Double price) {
        JpaPriceEntity entity = new JpaPriceEntity();
        entity.setId(id);
        entity.setBrandId(brandId);
        entity.setStartDate(LocalDateTime.now().minusDays(1));
        entity.setEndDate(LocalDateTime.now().plusDays(1));
        entity.setPriceList(priceList);
        entity.setProductId(productId);
        entity.setPriority(priority);
        entity.setPrice(price);
        entity.setCurrency("EUR");
        return entity;
    }
}
