package com.olist.dashboard.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Pandas {@code orient="list"} response for {@code GET /api/sellers/performance}. */
public record SellerPerformanceResponse(
        @JsonProperty("seller_id") List<String> sellerId,
        @JsonProperty("avg_review_score") List<Double> avgReviewScore,
        @JsonProperty("total_sales") List<Double> totalSales,
        @JsonProperty("num_orders") List<Long> numOrders) {

    public SellerPerformanceResponse {
        sellerId = immutableCopy(sellerId, "sellerId");
        avgReviewScore = immutableCopy(avgReviewScore, "avgReviewScore");
        totalSales = immutableCopy(totalSales, "totalSales");
        numOrders = immutableCopy(numOrders, "numOrders");
    }

    private static <T> List<T> immutableCopy(List<T> values, String fieldName) {
        Objects.requireNonNull(values, fieldName + " must not be null");
        return Collections.unmodifiableList(new ArrayList<>(values));
    }
}
