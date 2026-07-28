package com.olist.dashboard.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Pandas {@code orient="list"} response for {@code GET /api/sellers/distribution}. */
public record SellerDistributionResponse(
        @JsonProperty("bucket") List<String> bucket,
        @JsonProperty("seller_count") List<Long> sellerCount) {

    public SellerDistributionResponse {
        bucket = immutableCopy(bucket, "bucket");
        sellerCount = immutableCopy(sellerCount, "sellerCount");
    }

    private static <T> List<T> immutableCopy(List<T> values, String fieldName) {
        Objects.requireNonNull(values, fieldName + " must not be null");
        return Collections.unmodifiableList(new ArrayList<>(values));
    }
}
