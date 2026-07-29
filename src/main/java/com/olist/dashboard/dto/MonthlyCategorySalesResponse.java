package com.olist.dashboard.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * The pandas {@code to_json(orient="split")} contract used by monthly raw-category sales.
 *
 * <p>This deliberately orders properties as {@code columns}, {@code index}, and {@code data},
 * unlike the general {@link SplitMatrixResponse}; that is the serialized property order emitted
 * by the source endpoint.</p>
 */
@JsonPropertyOrder({"columns", "index", "data"})
public record MonthlyCategorySalesResponse(
        List<String> columns,
        List<String> index,
        List<List<Double>> data) {

    public MonthlyCategorySalesResponse {
        columns = immutableList(columns, "columns");
        index = immutableList(index, "index");
        Objects.requireNonNull(data, "data must not be null");
        var copiedData = new ArrayList<List<Double>>(data.size());
        for (var row : data) {
            copiedData.add(immutableList(row, "data row"));
        }
        data = Collections.unmodifiableList(copiedData);
    }

    private static <T> List<T> immutableList(List<T> values, String name) {
        Objects.requireNonNull(values, name + " must not be null");
        return Collections.unmodifiableList(new ArrayList<>(values));
    }
}
