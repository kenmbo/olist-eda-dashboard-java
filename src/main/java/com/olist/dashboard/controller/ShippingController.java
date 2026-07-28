package com.olist.dashboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olist.dashboard.dto.ShippingStagesByCityResponse;
import com.olist.dashboard.service.ShippingAnalyticsService;

/** HTTP routes for migrated shipping analytics. */
@RestController
@RequestMapping("/api/shipping")
public class ShippingController {

    private final ShippingAnalyticsService shippingAnalyticsService;

    public ShippingController(ShippingAnalyticsService shippingAnalyticsService) {
        this.shippingAnalyticsService = shippingAnalyticsService;
    }

    @GetMapping("/stages-by-city")
    public ShippingStagesByCityResponse stagesByCity() {
        return shippingAnalyticsService.stagesByCity();
    }
}
