package com.olist.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A pandas-style {@code orient="records"} response that serializes as a JSON array of typed row
 * records.
 *
 * <p>The wrapper does not introduce a {@code records} property; {@link JsonValue} retains the
 * source array-at-the-root contract.</p>
 *
 * @param <T> the endpoint-specific typed row record
 */
public record RecordOrientedResponse<T>(List<T> records) {

    public RecordOrientedResponse {
        Objects.requireNonNull(records, "records must not be null");
        records = Collections.unmodifiableList(new ArrayList<>(records));
    }

    /** Serializes the typed rows directly as the response array. */
    @JsonValue
    @Override
    public List<T> records() {
        return records;
    }
}
