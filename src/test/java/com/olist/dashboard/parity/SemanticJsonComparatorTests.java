package com.olist.dashboard.parity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.json.JsonMapper;

class SemanticJsonComparatorTests {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();
    private final SemanticJsonComparator comparator = new SemanticJsonComparator();

    @Test
    void acceptsExactShapeAndFloatsWithinTheDocumentedTolerance() throws Exception {
        var expected = jsonMapper.readTree("{\"day\":[\"2017-01-01\"],\"value\":[1233131.7199999709],\"count\":[2]}");
        var actual = jsonMapper.readTree("{\"day\":[\"2017-01-01\"],\"value\":[1233131.7200005],\"count\":[2]}");

        assertThat(comparator.compare(expected, actual)).isEmpty();
    }

    @Test
    void rejectsFieldOrderIntegerKindAndArrayOrderChanges() throws Exception {
        var expected = jsonMapper.readTree("{\"day\":[\"2017-01-01\",\"2017-01-02\"],\"count\":[2]}");
        var actual = jsonMapper.readTree("{\"count\":[2.0],\"day\":[\"2017-01-02\",\"2017-01-01\"]}");

        assertThat(comparator.compare(expected, actual))
                .extracting(ParityMismatch::detail)
                .anySatisfy(detail -> assertThat(detail).contains("field names or order"))
                .anySatisfy(detail -> assertThat(detail).contains("integer-versus-floating"))
                .anySatisfy(detail -> assertThat(detail).contains("scalar value differs"));
    }
}
