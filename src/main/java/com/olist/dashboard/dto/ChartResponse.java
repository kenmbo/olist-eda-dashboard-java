package com.olist.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A response made of endpoint-specific named chart arrays.
 *
 * <p>Unlike a generic envelope, this serializes each {@link ChartSeries#propertyName()} at the
 * JSON root. Series order becomes property order, matching the source's custom dictionary
 * responses.</p>
 */
public record ChartResponse(List<ChartSeries<?>> chartSeries) {

    public ChartResponse {
        Objects.requireNonNull(chartSeries, "chartSeries must not be null");
        var names = new java.util.HashSet<String>();
        for (var series : chartSeries) {
            Objects.requireNonNull(series, "chartSeries must not contain null elements");
            if (!names.add(series.propertyName())) {
                throw new IllegalArgumentException("chart series property names must be unique");
            }
        }
        chartSeries = Collections.unmodifiableList(new ArrayList<>(chartSeries));
    }

    /** Serializes the named chart arrays directly as the response object. */
    @JsonValue
    public Map<String, List<?>> jsonFields() {
        var fields = new LinkedHashMap<String, List<?>>();
        for (var series : chartSeries) {
            fields.put(series.propertyName(), series.values());
        }
        return fields;
    }
}
