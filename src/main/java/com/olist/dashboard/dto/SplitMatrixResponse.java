package com.olist.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A pandas-style {@code orient="split"} response with ordered row labels, column labels, and
 * matrix cells.
 *
 * <p>The generic component types make the response suitable for string or numeric labels and for
 * integer, decimal, or nullable matrix values. Matrix dimensions are intentionally not inferred or
 * repaired here: endpoint services must preserve the captured source dimensions exactly.</p>
 */
@JsonPropertyOrder({"index", "columns", "data"})
public record SplitMatrixResponse<I, C, V>(
        @JsonProperty("index") List<I> index,
        @JsonProperty("columns") List<C> columns,
        @JsonProperty("data") List<List<V>> data) {

    public SplitMatrixResponse {
        index = immutableRequiredList(index, "index");
        columns = immutableRequiredList(columns, "columns");
        data = immutableMatrix(data);
    }

    private static <T> List<T> immutableRequiredList(List<T> values, String name) {
        Objects.requireNonNull(values, name + " must not be null");
        return Collections.unmodifiableList(new ArrayList<>(values));
    }

    private static <T> List<List<T>> immutableMatrix(List<List<T>> matrix) {
        Objects.requireNonNull(matrix, "data must not be null");
        var copy = new ArrayList<List<T>>(matrix.size());
        for (var row : matrix) {
            copy.add(ColumnarResponse.immutableListPreservingNulls(row));
        }
        return Collections.unmodifiableList(copy);
    }
}
