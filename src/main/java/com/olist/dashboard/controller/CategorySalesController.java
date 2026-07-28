package com.olist.dashboard.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olist.dashboard.dto.ColumnarResponse;
import com.olist.dashboard.service.CategorySalesService;

/** HTTP endpoint for the source translated-category sales summary. */
@RestController
public class CategorySalesController {

    private final CategorySalesService categorySalesService;

    public CategorySalesController(CategorySalesService categorySalesService) {
        this.categorySalesService = categorySalesService;
    }

    @GetMapping(value = "/api/categories/sales", produces = MediaType.APPLICATION_JSON_VALUE)
    public ColumnarResponse categorySales() {
        return categorySalesService.categorySales();
    }
}
