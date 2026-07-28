package com.olist.dashboard.service;

import org.springframework.stereotype.Service;

import com.olist.dashboard.dto.ReviewDistributionResponse;
import com.olist.dashboard.repository.ReviewAnalyticsRepository;

/** Shapes source review-score counts into literal star labels and count arrays. */
@Service
public class ReviewAnalyticsService {

    private final ReviewAnalyticsRepository reviewAnalyticsRepository;
    private final PandasFormattingService pandasFormattingService;

    public ReviewAnalyticsService(
            ReviewAnalyticsRepository reviewAnalyticsRepository,
            PandasFormattingService pandasFormattingService) {
        this.reviewAnalyticsRepository = reviewAnalyticsRepository;
        this.pandasFormattingService = pandasFormattingService;
    }

    public ReviewDistributionResponse distribution() {
        var rows = reviewAnalyticsRepository.findDistribution();
        return new ReviewDistributionResponse(
                rows.stream().map(row -> pandasFormattingService.reviewScoreLabel(row.reviewScore())).toList(),
                rows.stream().map(row -> row.totalReviews()).toList());
    }
}
