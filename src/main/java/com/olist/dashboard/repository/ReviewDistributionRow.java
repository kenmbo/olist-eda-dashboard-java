package com.olist.dashboard.repository;

/** Raw review-score distribution row before star-label formatting. */
public record ReviewDistributionRow(Long reviewScore, Long totalReviews) {
}
