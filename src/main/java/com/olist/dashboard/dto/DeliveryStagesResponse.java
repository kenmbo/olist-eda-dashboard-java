package com.olist.dashboard.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** Custom chart-array contract for {@code GET /api/delivery/stages}. */
@JsonPropertyOrder({"cities", "approval_days", "carrier_days", "transit_days"})
public record DeliveryStagesResponse(
        List<String> cities,
        @JsonProperty("approval_days") List<Double> approvalDays,
        @JsonProperty("carrier_days") List<Double> carrierDays,
        @JsonProperty("transit_days") List<Double> transitDays) {
}
