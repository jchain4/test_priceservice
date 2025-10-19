package com.practice.priceservice.infrastructure.web.mapper;

import com.practice.priceservice.domain.Price;
import com.practice.priceservice.infrastructure.web.dto.PriceResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PriceMapperTest {

    private final PriceMapper mapper = Mappers.getMapper(PriceMapper.class);

    @Test
    void shouldMapPriceToResponse() {
        Price price = new Price(1L, 1, LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                2, 35455, 1, 25.45, "EUR");

        PriceResponse response = mapper.toResponse(price);

        assertEquals(price.getProductId(), response.getProductId());
        assertEquals(price.getBrandId(), response.getBrandId());
        assertEquals(price.getPriceList(), response.getPriceList());
        assertEquals(price.getPrice(), response.getPrice());
        assertEquals(price.getCurrency(), response.getCurrency());
    }
}
