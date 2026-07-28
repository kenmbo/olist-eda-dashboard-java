package com.olist.dashboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olist.dashboard.dto.CustomersClvMapResponse;
import com.olist.dashboard.service.CustomerAnalyticsService;

/** HTTP routes for migrated customer analytics. */
@RestController
@RequestMapping("/api/customers")
public class CustomersController {

    private final CustomerAnalyticsService customerAnalyticsService;

    public CustomersController(CustomerAnalyticsService customerAnalyticsService) {
        this.customerAnalyticsService = customerAnalyticsService;
    }

    @GetMapping("/clv-map")
    public CustomersClvMapResponse clvMap() {
        return customerAnalyticsService.clvMap();
    }
}
