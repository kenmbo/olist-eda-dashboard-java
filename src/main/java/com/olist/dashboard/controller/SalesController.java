package com.olist.dashboard.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olist.dashboard.dto.MonthlySalesResponse;
import com.olist.dashboard.service.MonthlySalesService;

/** HTTP route for the source's fixed translated-category monthly sales series. */
@RestController
public class SalesController {

    private final MonthlySalesService monthlySalesService;

    public SalesController(MonthlySalesService monthlySalesService) {
        this.monthlySalesService = monthlySalesService;
    }

    @GetMapping(value = "/api/sales/monthly", produces = MediaType.APPLICATION_JSON_VALUE)
    public MonthlySalesResponse monthlySales() {
        return monthlySalesService.monthlySales();
    }
}
