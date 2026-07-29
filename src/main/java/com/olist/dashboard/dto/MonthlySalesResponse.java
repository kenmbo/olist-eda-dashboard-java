package com.olist.dashboard.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** pandas {@code orient="list"} contract for {@code GET /api/sales/monthly}. */
@JsonPropertyOrder({"year_month", "health_beauty", "auto", "toys", "electronics", "fashion_shoes"})
public record MonthlySalesResponse(
        @JsonProperty("year_month") List<String> yearMonth,
        @JsonProperty("health_beauty") List<Double> healthBeauty,
        List<Double> auto,
        List<Double> toys,
        List<Double> electronics,
        @JsonProperty("fashion_shoes") List<Double> fashionShoes) {
}
