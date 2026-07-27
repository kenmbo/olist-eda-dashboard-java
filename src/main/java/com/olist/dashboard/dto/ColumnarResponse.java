package com.olist.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A pandas-style {@code orient="list"} response.
 *
 * <p>The map itself is the JSON document, so its entries become top-level response properties
 * rather than being nested below a Java-specific wrapper name. A {@link LinkedHashMap} copy keeps
 * the caller's deliberate property order, which is part of the captured FastAPI contract.</p>
 */
public record ColumnarResponse(Map<String, List<?>> columns) {

    public ColumnarResponse {
        Objects.requireNonNull(columns, "columns must not be null");
        var orderedColumns = new LinkedHashMap<String, List<?>>();
        columns.forEach((name, values) -> {
            Objects.requireNonNull(name, "column names must not be null");
            orderedColumns.put(name, immutableListPreservingNulls(values));
        });
        columns = Collections.unmodifiableMap(orderedColumns);
    }

    /** Serializes the column map directly as the response object. */
    @JsonValue
    @Override
    public Map<String, List<?>> columns() {
        return columns;
    }

    static <T> List<T> immutableListPreservingNulls(List<T> values) {
        return values == null ? null : Collections.unmodifiableList(new ArrayList<>(values));
    }
}
