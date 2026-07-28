package com.olist.dashboard.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** Custom chart-array contract for {@code GET /api/sellers/review-sales}. */
@JsonPropertyOrder({"seller_ids", "total_sales", "avg_scores", "order_counts"})
public record SellerReviewSalesResponse(
        @JsonProperty("seller_ids") List<String> sellerIds,
        @JsonProperty("total_sales") List<Double> totalSales,
        @JsonProperty("avg_scores") List<Double> averageScores,
        @JsonProperty("order_counts") List<Long> orderCounts) {
}
