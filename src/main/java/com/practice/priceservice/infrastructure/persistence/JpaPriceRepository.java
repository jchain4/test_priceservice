package com.practice.priceservice.infrastructure.persistence;

import com.practice.priceservice.infrastructure.persistence.JpaPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaPriceRepository extends JpaRepository<JpaPriceEntity, Long> {

    @Query("SELECT p FROM JpaPriceEntity p WHERE :date BETWEEN p.startDate AND p.endDate AND p.productId = :productId AND p.brandId = :brandId")
    List<JpaPriceEntity> findApplicablePrices(@Param("date") LocalDateTime date,
                                              @Param("productId") Integer productId,
                                              @Param("brandId") Integer brandId);
}
