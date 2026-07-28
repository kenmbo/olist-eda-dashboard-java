package com.olist.dashboard.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** Custom chart-array contract for {@code GET /api/leads/conversion}. */
@JsonPropertyOrder({"origins", "qualified_leads", "closed_leads", "conversion_rate"})
public record LeadConversionResponse(
        List<String> origins,
        @JsonProperty("qualified_leads") List<Long> qualifiedLeads,
        @JsonProperty("closed_leads") List<Long> closedLeads,
        @JsonProperty("conversion_rate") List<Double> conversionRate) {
}
