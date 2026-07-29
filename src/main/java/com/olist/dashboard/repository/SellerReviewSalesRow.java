package com.olist.dashboard.repository;

/** Raw seller review-versus-sales row before pandas-equivalent rounding. */
public record SellerReviewSalesRow(String sellerId, Double totalSales, Double averageScore, Long orderCount) {
}
