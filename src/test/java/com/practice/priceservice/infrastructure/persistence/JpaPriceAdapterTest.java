package com.practice.priceservice.infrastructure.persistence;

import com.practice.priceservice.domain.Price;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JpaPriceAdapterTest {

    @Test
    void shouldConvertJpaEntitiesToDomainPrices() {
        JpaPriceRepository mockJpaRepo = mock(JpaPriceRepository.class);
        JpaPriceAdapter adapter = new JpaPriceAdapter(mockJpaRepo);

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

        when(mockJpaRepo.findApplicablePrices(date, productId, brandId))
                .thenReturn(List.of(entity));

        List<Price> result = adapter.findApplicablePrices(date, productId, brandId);

        assertEquals(1, result.size());
        Price price = result.get(0);
        assertEquals(entity.getId(), price.getId());
        assertEquals(entity.getPrice(), price.getPrice());
        assertEquals(entity.getCurrency(), price.getCurrency());
    }
}
