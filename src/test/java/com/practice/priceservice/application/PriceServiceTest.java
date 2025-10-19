package com.practice.priceservice.application;

import com.practice.priceservice.domain.Price;
import com.practice.priceservice.domain.PriceRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PriceServiceTest {

    @Test
    void shouldReturnHighestPriorityPrice() {
        PriceRepository mockRepo = mock(PriceRepository.class);
        PriceService service = new PriceService(mockRepo);

        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 16, 0);
        Integer productId = 35455;
        Integer brandId = 1;

        Price lowPriority = new Price(1L, brandId, date.minusHours(1), date.plusHours(1), 1, productId, 0, 35.50, "EUR");
        Price highPriority = new Price(2L, brandId, date.minusHours(1), date.plusHours(1), 2, productId, 1, 25.45, "EUR");

        when(mockRepo.findApplicablePrices(date, productId, brandId))
                .thenReturn(List.of(lowPriority, highPriority));

        Optional<Price> result = service.getBestPrice(date, productId, brandId);

        assertTrue(result.isPresent());
        assertEquals(2L, result.get().getId());
        assertEquals(1, result.get().getPriority());
    }
}
