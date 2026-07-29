package com.olist.dashboard.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olist.dashboard.dto.ColumnarResponse;
import com.olist.dashboard.dto.SplitMatrixResponse;
import com.olist.dashboard.service.OrdersService;

/** HTTP endpoints whose source implementations directly query order analytics SQL. */
@RestController
public class OrdersController {

    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @GetMapping(value = "/api/orders/daily", produces = MediaType.APPLICATION_JSON_VALUE)
    public ColumnarResponse dailyOrderCounts() {
        return ordersService.dailyOrderCounts();
    }

    @GetMapping(value = "/api/orders/hourly", produces = MediaType.APPLICATION_JSON_VALUE)
    public SplitMatrixResponse<String, String, Long> hourlyOrderCounts() {
        return ordersService.hourlyOrderCounts();
    }

    @GetMapping(value = "/api/orders/costs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ColumnarResponse deliveredOrderCosts() {
        return ordersService.deliveredOrderCosts();
    }
}
