package com.olist.dashboard.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olist.dashboard.dto.SellerDistributionResponse;
import com.olist.dashboard.service.SellerDistributionService;

/** HTTP adapter for the seller order-volume distribution chart. */
@RestController
public class SellerDistributionController {

    private final SellerDistributionService sellerDistributionService;

    public SellerDistributionController(SellerDistributionService sellerDistributionService) {
        this.sellerDistributionService = sellerDistributionService;
    }

    @GetMapping(path = "/api/sellers/distribution", produces = MediaType.APPLICATION_JSON_VALUE)
    public SellerDistributionResponse getSellerDistribution() {
        return sellerDistributionService.getDistribution();
    }
}
