package com.practice.priceservice.application;

import com.practice.priceservice.domain.Price;
import com.practice.priceservice.domain.PriceRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

public class PriceService {

    private final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public Optional<Price> getBestPrice(LocalDateTime date, Integer productId, Integer brandId) {
        return priceRepository.findApplicablePrices(date, productId, brandId).stream()
                .max(Comparator.comparingInt(Price::getPriority));
    }
}
