package com.olist.dashboard.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olist.dashboard.dto.SellerReviewSalesResponse;
import com.olist.dashboard.service.SellerReviewSalesService;

/** HTTP adapter for the seller review-versus-sales chart data. */
@RestController
public class SellerReviewSalesController {

    private final SellerReviewSalesService sellerReviewSalesService;

    public SellerReviewSalesController(SellerReviewSalesService sellerReviewSalesService) {
        this.sellerReviewSalesService = sellerReviewSalesService;
    }

    @GetMapping(value = "/api/sellers/review-sales", produces = MediaType.APPLICATION_JSON_VALUE)
    public SellerReviewSalesResponse reviewSales() {
        return sellerReviewSalesService.reviewSales();
    }
}
