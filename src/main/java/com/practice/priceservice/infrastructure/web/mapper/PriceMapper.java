package com.practice.priceservice.infrastructure.web.mapper;

import com.practice.priceservice.domain.Price;
import com.practice.priceservice.infrastructure.web.dto.PriceResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceMapper {
    PriceResponse toResponse(Price price);
}
