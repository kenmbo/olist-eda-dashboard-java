package com.olist.dashboard.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.olist.dashboard.dto.CustomersClvMapResponse;
import com.olist.dashboard.dto.ShippingStagesByCityResponse;
import com.olist.dashboard.service.CustomerAnalyticsService;
import com.olist.dashboard.service.ShippingAnalyticsService;
import com.olist.dashboard.support.JsonContractAssertions;

import tools.jackson.databind.json.JsonMapper;

class ShippingCustomersControllerTests {

    private final ShippingAnalyticsService shippingAnalyticsService = mock(ShippingAnalyticsService.class);
    private final CustomerAnalyticsService customerAnalyticsService = mock(CustomerAnalyticsService.class);
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
            new ShippingController(shippingAnalyticsService),
            new CustomersController(customerAnalyticsService))
            .build();
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Test
    void stagesByCityUsesTheExactSourcePathContentTypeKeysAndColumnOrder() throws Exception {
        when(shippingAnalyticsService.stagesByCity()).thenReturn(new ShippingStagesByCityResponse(
                java.util.List.of("SALVADOR", "SAO PAULO"),
                java.util.List.of(1.5, 1.0),
                java.util.List.of(2.5, 1.0),
                java.util.List.of(4.5, 2.0),
                java.util.List.of(2.5, 2.0)));

        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/shipping/stages-by-city"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        var json = jsonMapper.readTree(response.getContentAsString());
        JsonContractAssertions.hasExactObjectKeys(
                json, "city", "approved", "delivered_to_carrier", "delivered_to_customer", "estimated_delivery");
        JsonContractAssertions.hasParallelArrayLengths(
                json, "city", "approved", "delivered_to_carrier", "delivered_to_customer", "estimated_delivery");
        assertThat(response.getContentAsString()).isEqualTo(
                "{\"city\":[\"SALVADOR\",\"SAO PAULO\"],\"approved\":[1.5,1.0],"
                        + "\"delivered_to_carrier\":[2.5,1.0],\"delivered_to_customer\":[4.5,2.0],"
                        + "\"estimated_delivery\":[2.5,2.0]}");
    }

    @Test
    void clvMapUsesTheExactSourcePathCapitalizationAndIntegerJsonColumns() throws Exception {
        when(customerAnalyticsService.clvMap()).thenReturn(new CustomersClvMapResponse(
                java.util.List.of(1003, 99990),
                java.util.List.of(425.0, 300.0),
                java.util.List.of(2L, 1L),
                java.util.List.of(2.0, 6.0),
                java.util.List.of(12.0, 16.0)));

        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/clv-map"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        var json = jsonMapper.readTree(response.getContentAsString());
        JsonContractAssertions.hasExactObjectKeys(
                json, "zip_prefix", "avg_CLV", "customer_count", "latitude", "longitude");
        JsonContractAssertions.hasParallelArrayLengths(
                json, "zip_prefix", "avg_CLV", "customer_count", "latitude", "longitude");
        assertThat(json.get("zip_prefix").get(0).isInt()).isTrue();
        assertThat(json.get("customer_count").get(0).isIntegralNumber()).isTrue();
        assertThat(response.getContentAsString()).isEqualTo(
                "{\"zip_prefix\":[1003,99990],\"avg_CLV\":[425.0,300.0],\"customer_count\":[2,1],"
                        + "\"latitude\":[2.0,6.0],\"longitude\":[12.0,16.0]}");
    }
}
