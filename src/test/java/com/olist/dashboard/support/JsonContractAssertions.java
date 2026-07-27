package com.olist.dashboard.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import tools.jackson.databind.JsonNode;

/** Reusable exact-shape assertions for future MockMvc contract tests. */
public final class JsonContractAssertions {

    private JsonContractAssertions() {
    }

    public static void hasExactObjectKeys(JsonNode object, String... expectedKeys) {
        assertThat(object.isObject()).as("JSON object").isTrue();
        assertThat(new ArrayList<>(object.propertyNames()))
                .as("JSON object key sequence")
                .containsExactly(expectedKeys);
    }

    public static void hasParallelArrayLengths(JsonNode object, String... fieldNames) {
        assertThat(object.isObject()).as("JSON object").isTrue();
        Integer expectedLength = null;
        for (String fieldName : fieldNames) {
            JsonNode field = object.get(fieldName);
            assertThat(field).as("field %s", fieldName).isNotNull();
            assertThat(field.isArray()).as("field %s is an array", fieldName).isTrue();
            if (expectedLength == null) {
                expectedLength = field.size();
            } else {
                assertThat(field.size())
                        .as("field %s length", fieldName)
                        .isEqualTo(expectedLength);
            }
        }
    }

    public static void hasSplitMatrixDimensions(JsonNode response, int expectedRows, int expectedColumns) {
        hasExactObjectKeys(response, "index", "columns", "data");
        JsonNode index = response.get("index");
        JsonNode columns = response.get("columns");
        JsonNode data = response.get("data");

        assertThat(index.isArray()).as("split index").isTrue();
        assertThat(columns.isArray()).as("split columns").isTrue();
        assertThat(data.isArray()).as("split data").isTrue();
        assertThat(index.size()).isEqualTo(expectedRows);
        assertThat(columns.size()).isEqualTo(expectedColumns);
        assertThat(data.size()).isEqualTo(expectedRows);
        for (int row = 0; row < data.size(); row++) {
            JsonNode values = data.get(row);
            assertThat(values.isArray()).as("split data row %s", row).isTrue();
            assertThat(values.size()).as("split data row %s width", row).isEqualTo(expectedColumns);
        }
    }
}
