package com.olist.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.olist.dashboard.config.OlistJacksonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(OlistJacksonConfiguration.class)
class SharedResponseSerializationTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void columnarResponseSerializesOrderedRootPropertiesAndRetainsNullArrayValues() throws Exception {
        var columns = new LinkedHashMap<String, List<?>>();
        columns.put("day", Arrays.asList("2016-09-04", null));
        columns.put("order_count", List.of(1, 2));

        var json = objectMapper.writeValueAsString(new ColumnarResponse(columns));

        assertThat(json).isEqualTo("{\"day\":[\"2016-09-04\",null],\"order_count\":[1,2]}");
    }

    @Test
    void splitMatrixResponseSerializesExactSplitKeysAndNullableCells() throws Exception {
        var response = new SplitMatrixResponse<>(
                List.of("Sun", "Mon"),
                List.of("0", "1"),
                List.of(List.of(1, 0), Arrays.asList(2, null)));

        var json = objectMapper.writeValueAsString(response);

        assertThat(json).isEqualTo(
                "{\"index\":[\"Sun\",\"Mon\"],\"columns\":[\"0\",\"1\"],\"data\":[[1,0],[2,null]]}");
    }

    @Test
    void recordOrientedResponseSerializesTypedRowsAsARootArrayWithExactPropertyNames() throws Exception {
        var response = new RecordOrientedResponse<>(Arrays.asList(
                new SampleRecord(12.5, null),
                null));

        var json = objectMapper.writeValueAsString(response);

        assertThat(json).isEqualTo("[{\"avg_CLV\":12.5,\"optional_label\":null},null]");
    }

    @Test
    void chartResponseSerializesCustomNamedArraysAtTheRootInSeriesOrder() throws Exception {
        var response = new ChartResponse(List.of(
                new ChartSeries<>("seller_ids", List.of("seller-a", "seller-b")),
                new ChartSeries<>("avg_scores", Arrays.asList(4.2, null)),
                new ChartSeries<>("order_counts", List.of(6, 8))));

        var json = objectMapper.writeValueAsString(response);

        assertThat(json).isEqualTo(
                "{\"seller_ids\":[\"seller-a\",\"seller-b\"],\"avg_scores\":[4.2,null],\"order_counts\":[6,8]}");
    }

    @Test
    void jacksonUsesIsoDateStringsExactPropertiesJsonNumbersAndIncludedNulls() throws Exception {
        var response = new SerializationPolicyRecord(
                LocalDate.of(2017, 1, 1),
                LocalDateTime.of(2017, 1, 1, 0, 0),
                new BigDecimal("0.0000012300"),
                1_233_131.7199999709d,
                null);

        var json = objectMapper.writeValueAsString(response);

        assertThat(json).isEqualTo(
                "{\"day\":\"2017-01-01\",\"yearMonth\":\"2017-01-01T00:00:00\","
                        + "\"preciseValue\":0.0000012300,\"unroundedValue\":1233131.7199999709,\"nullableValue\":null}");
    }

    private record SampleRecord(
            @JsonProperty("avg_CLV") Double averageClv,
            @JsonProperty("optional_label") String optionalLabel) {
    }

    private record SerializationPolicyRecord(
            LocalDate day,
            LocalDateTime yearMonth,
            BigDecimal preciseValue,
            double unroundedValue,
            Double nullableValue) {
    }
}
