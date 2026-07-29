package com.olist.dashboard.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** pandas {@code orient="list"} contract for filtered seller delivery durations. */
@JsonPropertyOrder({"bucket", "seller_id", "delivery_time"})
public record SellerShippingTimesResponse(
        List<String> bucket,
        @JsonProperty("seller_id") List<String> sellerId,
        @JsonProperty("delivery_time") List<Double> deliveryTime) {
}
