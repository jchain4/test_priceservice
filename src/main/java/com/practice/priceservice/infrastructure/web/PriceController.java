package com.practice.priceservice.infrastructure.web;

import com.practice.priceservice.application.PriceService;
import com.practice.priceservice.infrastructure.web.dto.PriceResponse;
import com.practice.priceservice.infrastructure.web.mapper.PriceMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/prices")
@Validated
public class PriceController {

    private final PriceService priceService;
    private final PriceMapper priceMapper;

    public PriceController(PriceService priceService, PriceMapper priceMapper) {
        this.priceService = priceService;
        this.priceMapper = priceMapper;
    }

    @GetMapping
    public ResponseEntity<PriceResponse> getPrice(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
            @RequestParam @NotNull @Positive Integer productId,
            @RequestParam @NotNull @Positive Integer brandId) {

        return priceService.getBestPrice(date, productId, brandId)
                .map(priceMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
