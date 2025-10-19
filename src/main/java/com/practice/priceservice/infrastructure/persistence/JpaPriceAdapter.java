package com.practice.priceservice.infrastructure.persistence;

import com.practice.priceservice.domain.Price;
import com.practice.priceservice.domain.PriceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class JpaPriceAdapter implements PriceRepository {

    private final JpaPriceRepository jpaRepository;

    public JpaPriceAdapter(JpaPriceRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Price> findApplicablePrices(LocalDateTime date, Integer productId, Integer brandId) {
        return jpaRepository.findApplicablePrices(date, productId, brandId).stream()
                .map(entity -> new Price(
                        entity.getId(),
                        entity.getBrandId(),
                        entity.getStartDate(),
                        entity.getEndDate(),
                        entity.getPriceList(),
                        entity.getProductId(),
                        entity.getPriority(),
                        entity.getPrice(),
                        entity.getCurrency()
                ))
                .collect(Collectors.toList());
    }
}
