package com.olist.dashboard.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olist.dashboard.dto.MonthlyCategorySalesResponse;
import com.olist.dashboard.service.MonthlyCategorySalesService;

/** HTTP route for the source raw-category monthly top-five pivot. */
@RestController
public class MonthlyCategorySalesController {

    private final MonthlyCategorySalesService monthlyCategorySalesService;

    public MonthlyCategorySalesController(MonthlyCategorySalesService monthlyCategorySalesService) {
        this.monthlyCategorySalesService = monthlyCategorySalesService;
    }

    @GetMapping(value = "/api/categories/monthly-sales", produces = MediaType.APPLICATION_JSON_VALUE)
    public MonthlyCategorySalesResponse monthlyCategorySales() {
        return monthlyCategorySalesService.monthlyCategorySales();
    }
}
