package com.olist.dashboard.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olist.dashboard.dto.DeliveryStagesResponse;
import com.olist.dashboard.service.DeliveryAnalyticsService;

/** HTTP routes for migrated delivery analytics. */
@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryAnalyticsService deliveryAnalyticsService;

    public DeliveryController(DeliveryAnalyticsService deliveryAnalyticsService) {
        this.deliveryAnalyticsService = deliveryAnalyticsService;
    }

    @GetMapping(value = "/stages", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeliveryStagesResponse stages() {
        return deliveryAnalyticsService.stages();
    }
}
