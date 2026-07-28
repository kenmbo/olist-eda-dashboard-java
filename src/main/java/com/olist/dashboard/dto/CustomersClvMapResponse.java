package com.olist.dashboard.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** pandas {@code orient="list"} contract for {@code GET /api/customers/clv-map}. */
@JsonPropertyOrder({"zip_prefix", "avg_CLV", "customer_count", "latitude", "longitude"})
public record CustomersClvMapResponse(
        @JsonProperty("zip_prefix") List<Integer> zipPrefix,
        @JsonProperty("avg_CLV") List<Double> averageClv,
        @JsonProperty("customer_count") List<Long> customerCount,
        List<Double> latitude,
        List<Double> longitude) {
}
