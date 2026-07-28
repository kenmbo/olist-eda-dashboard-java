package com.olist.dashboard.dto;

import java.util.List;

/** Custom chart-array contract for {@code GET /api/leads/origin}. */
public record LeadOriginResponse(List<String> origins, List<Long> leads) {
}
