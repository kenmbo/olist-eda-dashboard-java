package com.olist.dashboard.support;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.json.JsonMapper;

class JsonContractAssertionsTests {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Test
    void verifiesExactKeyOrderParallelArraysAndSplitMatrixDimensions() throws Exception {
        var columnar = jsonMapper.readTree("{\"day\":[\"2017-01-01\",\"2017-01-02\"],\"order_count\":[1,2]}");
        JsonContractAssertions.hasExactObjectKeys(columnar, "day", "order_count");
        JsonContractAssertions.hasParallelArrayLengths(columnar, "day", "order_count");

        var split = jsonMapper.readTree("{\"index\":[\"Sun\",\"Mon\"],\"columns\":[0,1],\"data\":[[1,2],[3,4]]}");
        JsonContractAssertions.hasSplitMatrixDimensions(split, 2, 2);
    }

    @Test
    void rejectsUnexpectedKeyOrderAndJaggedSplitData() throws Exception {
        var reordered = jsonMapper.readTree("{\"order_count\":[1],\"day\":[\"2017-01-01\"]}");
        assertThatThrownBy(() -> JsonContractAssertions.hasExactObjectKeys(reordered, "day", "order_count"))
                .isInstanceOf(AssertionError.class);

        var jagged = jsonMapper.readTree("{\"index\":[\"Sun\",\"Mon\"],\"columns\":[0,1],\"data\":[[1,2],[3]]}");
        assertThatThrownBy(() -> JsonContractAssertions.hasSplitMatrixDimensions(jagged, 2, 2))
                .isInstanceOf(AssertionError.class);
    }
}
