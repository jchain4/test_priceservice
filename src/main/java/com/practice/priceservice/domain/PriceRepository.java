package com.practice.priceservice.domain;


import java.time.LocalDateTime;
import java.util.List;

public interface PriceRepository {
    List<Price> findApplicablePrices(LocalDateTime date, Integer productId, Integer brandId);
}
