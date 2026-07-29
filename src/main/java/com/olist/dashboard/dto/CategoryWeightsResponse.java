package com.olist.dashboard.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

/** Ordered, dynamically named category arrays for {@code GET /api/categories/weights}. */
public record CategoryWeightsResponse(Map<String, List<Double>> categories) {

    public CategoryWeightsResponse {
        Objects.requireNonNull(categories, "categories must not be null");
        var ordered = new LinkedHashMap<String, List<Double>>();
        categories.forEach((category, weights) -> {
            Objects.requireNonNull(category, "category keys must not be null");
            Objects.requireNonNull(weights, "weight arrays must not be null");
            ordered.put(category, Collections.unmodifiableList(new ArrayList<>(weights)));
        });
        categories = Collections.unmodifiableMap(ordered);
    }

    /** Serializes the ordered category map directly rather than wrapping it in a Java field name. */
    @JsonValue
    @Override
    public Map<String, List<Double>> categories() {
        return categories;
    }
}
