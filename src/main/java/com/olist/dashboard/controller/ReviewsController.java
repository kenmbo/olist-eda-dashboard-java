package com.olist.dashboard.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olist.dashboard.dto.ReviewDistributionResponse;
import com.olist.dashboard.service.ReviewAnalyticsService;

/** HTTP routes for migrated review analytics. */
@RestController
@RequestMapping("/api/reviews")
public class ReviewsController {

    private final ReviewAnalyticsService reviewAnalyticsService;

    public ReviewsController(ReviewAnalyticsService reviewAnalyticsService) {
        this.reviewAnalyticsService = reviewAnalyticsService;
    }

    @GetMapping(value = "/distribution", produces = MediaType.APPLICATION_JSON_VALUE)
    public ReviewDistributionResponse distribution() {
        return reviewAnalyticsService.distribution();
    }
}
