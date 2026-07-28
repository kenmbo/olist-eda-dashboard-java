package com.olist.dashboard.service;

import org.springframework.stereotype.Service;

import com.olist.dashboard.dto.ShippingStagesByCityResponse;
import com.olist.dashboard.repository.ShippingAnalyticsRepository;

/** Shapes source-query shipping rows into their frozen column-oriented HTTP contract. */
@Service
public class ShippingAnalyticsService {

    private final ShippingAnalyticsRepository shippingAnalyticsRepository;

    public ShippingAnalyticsService(ShippingAnalyticsRepository shippingAnalyticsRepository) {
        this.shippingAnalyticsRepository = shippingAnalyticsRepository;
    }

    public ShippingStagesByCityResponse stagesByCity() {
        var rows = shippingAnalyticsRepository.findStagesByCity();
        return new ShippingStagesByCityResponse(
                rows.stream().map(row -> row.city()).toList(),
                rows.stream().map(row -> row.approved()).toList(),
                rows.stream().map(row -> row.deliveredToCarrier()).toList(),
                rows.stream().map(row -> row.deliveredToCustomer()).toList(),
                rows.stream().map(row -> row.estimatedDelivery()).toList());
    }
}
