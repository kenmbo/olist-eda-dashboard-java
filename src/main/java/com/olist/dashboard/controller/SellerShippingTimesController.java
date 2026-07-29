package com.olist.dashboard.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olist.dashboard.dto.SellerShippingTimesResponse;
import com.olist.dashboard.service.SellerShippingTimesService;

/** HTTP route for source-equivalent per-seller delivery-time IQR filtering. */
@RestController
public class SellerShippingTimesController {

    private final SellerShippingTimesService sellerShippingTimesService;

    public SellerShippingTimesController(SellerShippingTimesService sellerShippingTimesService) {
        this.sellerShippingTimesService = sellerShippingTimesService;
    }

    @GetMapping(value = "/api/sellers/shipping-times", produces = MediaType.APPLICATION_JSON_VALUE)
    public SellerShippingTimesResponse shippingTimes() {
        return sellerShippingTimesService.shippingTimes();
    }
}
