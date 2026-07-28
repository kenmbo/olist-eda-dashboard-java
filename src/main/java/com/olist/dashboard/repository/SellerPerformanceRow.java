package com.olist.dashboard.repository;

/** One source-query row for seller review, sales, and order-volume metrics. */
public record SellerPerformanceRow(
        String sellerId,
        Double avgReviewScore,
        Double totalSales,
        long numOrders) {
}
