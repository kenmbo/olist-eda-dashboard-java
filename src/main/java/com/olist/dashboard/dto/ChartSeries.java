package com.olist.dashboard.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * One named array in a chart-specific response.
 *
 * <p>The {@code propertyName} is deliberately supplied by the endpoint contract. This supports
 * source names such as {@code seller_ids}, {@code avg_CLV}, and {@code approval_days} without a
 * global naming strategy silently changing them.</p>
 *
 * @param <T> the values in the series
 */
public record ChartSeries<T>(String propertyName, List<T> values) {

    public ChartSeries {
        Objects.requireNonNull(propertyName, "propertyName must not be null");
        if (propertyName.isBlank()) {
            throw new IllegalArgumentException("propertyName must not be blank");
        }
        Objects.requireNonNull(values, "values must not be null");
        values = Collections.unmodifiableList(new ArrayList<>(values));
    }
}
