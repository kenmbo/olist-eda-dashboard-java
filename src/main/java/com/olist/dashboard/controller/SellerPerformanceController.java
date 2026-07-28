package com.olist.dashboard.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olist.dashboard.dto.SellerPerformanceResponse;
import com.olist.dashboard.service.SellerPerformanceService;

/** HTTP adapter for the seller performance scatter-plot data. */
@RestController
public class SellerPerformanceController {

    private final SellerPerformanceService sellerPerformanceService;

    public SellerPerformanceController(SellerPerformanceService sellerPerformanceService) {
        this.sellerPerformanceService = sellerPerformanceService;
    }

    @GetMapping(path = "/api/sellers/performance", produces = MediaType.APPLICATION_JSON_VALUE)
    public SellerPerformanceResponse getSellerPerformance() {
        return sellerPerformanceService.getPerformance();
    }
}
