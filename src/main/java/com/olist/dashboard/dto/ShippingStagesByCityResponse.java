package com.olist.dashboard.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** pandas {@code orient="list"} contract for {@code GET /api/shipping/stages-by-city}. */
@JsonPropertyOrder({"city", "approved", "delivered_to_carrier", "delivered_to_customer", "estimated_delivery"})
public record ShippingStagesByCityResponse(
        List<String> city,
        List<Double> approved,
        @JsonProperty("delivered_to_carrier") List<Double> deliveredToCarrier,
        @JsonProperty("delivered_to_customer") List<Double> deliveredToCustomer,
        @JsonProperty("estimated_delivery") List<Double> estimatedDelivery) {
}
