package com.olist.dashboard.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olist.dashboard.dto.CategoryWeightsResponse;
import com.olist.dashboard.service.CategoryWeightsService;

/** HTTP route for the approved filtered category-weight contract. */
@RestController
public class CategoryWeightsController {

    private final CategoryWeightsService categoryWeightsService;

    public CategoryWeightsController(CategoryWeightsService categoryWeightsService) {
        this.categoryWeightsService = categoryWeightsService;
    }

    @GetMapping(value = "/api/categories/weights", produces = MediaType.APPLICATION_JSON_VALUE)
    public CategoryWeightsResponse categoryWeights() {
        return categoryWeightsService.categoryWeights();
    }
}
