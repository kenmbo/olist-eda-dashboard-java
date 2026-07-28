package com.olist.dashboard.dto;

import java.util.List;

/** Custom chart-array contract for {@code GET /api/reviews/distribution}. */
public record ReviewDistributionResponse(List<String> scores, List<Long> counts) {
}
