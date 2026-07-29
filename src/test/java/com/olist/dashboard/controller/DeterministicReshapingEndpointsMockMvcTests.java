package com.olist.dashboard.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.olist.dashboard.dto.CategoryWeightsResponse;
import com.olist.dashboard.dto.MonthlyCategorySalesResponse;
import com.olist.dashboard.dto.MonthlySalesResponse;
import com.olist.dashboard.dto.SellerShippingTimesResponse;
import com.olist.dashboard.dto.SplitMatrixResponse;
import com.olist.dashboard.error.ApiExceptionHandler;
import com.olist.dashboard.service.CategoryWeightsService;
import com.olist.dashboard.service.MonthlyCategorySalesService;
import com.olist.dashboard.service.MonthlySalesService;
import com.olist.dashboard.service.OrdersService;
import com.olist.dashboard.service.SellerShippingTimesService;
import com.olist.dashboard.support.JsonContractAssertions;

import tools.jackson.databind.json.JsonMapper;

/** Exact successful HTTP-shape tests for all five Milestone 5 routes. */
class DeterministicReshapingEndpointsMockMvcTests {

    private final OrdersService ordersService = mock(OrdersService.class);
    private final MonthlySalesService monthlySalesService = mock(MonthlySalesService.class);
    private final MonthlyCategorySalesService monthlyCategorySalesService = mock(MonthlyCategorySalesService.class);
    private final SellerShippingTimesService sellerShippingTimesService = mock(SellerShippingTimesService.class);
    private final CategoryWeightsService categoryWeightsService = mock(CategoryWeightsService.class);
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
            new OrdersController(ordersService),
            new SalesController(monthlySalesService),
            new MonthlyCategorySalesController(monthlyCategorySalesService),
            new SellerShippingTimesController(sellerShippingTimesService),
            new CategoryWeightsController(categoryWeightsService))
            .setControllerAdvice(new ApiExceptionHandler())
            .build();
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Test
    void hourlyOrdersUsesSplitKeyOrderStringHoursAndIntegralMatrixCells() throws Exception {
        when(ordersService.hourlyOrderCounts()).thenReturn(new SplitMatrixResponse<>(
                List.of("Sun"), List.of("0", "1"), List.of(List.of(1L, 0L))));

        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/hourly")).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString()).isEqualTo(
                "{\"index\":[\"Sun\"],\"columns\":[\"0\",\"1\"],\"data\":[[1,0]]}");
        var json = jsonMapper.readTree(response.getContentAsString());
        JsonContractAssertions.hasExactObjectKeys(json, "index", "columns", "data");
        assertThat(json.get("data").get(0).get(0).isIntegralNumber()).isTrue();
    }

    @Test
    void monthlySalesUsesExactIsoDateAndColumnKeyOrder() throws Exception {
        when(monthlySalesService.monthlySales()).thenReturn(new MonthlySalesResponse(
                List.of("2017-01-01T00:00:00"),
                List.of(10.0), List.of(20.0), List.of(30.0), List.of(40.0), List.of(50.0)));

        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/sales/monthly")).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString()).isEqualTo(
                "{\"year_month\":[\"2017-01-01T00:00:00\"],\"health_beauty\":[10.0],\"auto\":[20.0],"
                        + "\"toys\":[30.0],\"electronics\":[40.0],\"fashion_shoes\":[50.0]}");
        JsonContractAssertions.hasExactObjectKeys(jsonMapper.readTree(response.getContentAsString()),
                "year_month", "health_beauty", "auto", "toys", "electronics", "fashion_shoes");
    }

    @Test
    void monthlySalesUsesTheDocumentedSanitizedServerErrorWhenSourceWouldSerializeNaN() throws Exception {
        when(monthlySalesService.monthlySales()).thenThrow(
                new IllegalStateException("A selected monthly category aggregate is missing"));

        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/sales/monthly"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString()).isEqualTo("{\"error\":\"Internal server error.\"}");
    }

    @Test
    void monthlyCategorySalesUsesPandasSplitPropertyOrderAndFloatingZeroes() throws Exception {
        when(monthlyCategorySalesService.monthlyCategorySales()).thenReturn(new MonthlyCategorySalesResponse(
                List.of("a", "b"), List.of("2017-01"), List.of(List.of(1.25, 0.0))));

        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/monthly-sales"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString()).isEqualTo(
                "{\"columns\":[\"a\",\"b\"],\"index\":[\"2017-01\"],\"data\":[[1.25,0.0]]}");
        JsonContractAssertions.hasExactObjectKeys(jsonMapper.readTree(response.getContentAsString()), "columns", "index", "data");
    }

    @Test
    void sellerShippingTimesUsesSourceColumnNamesAndParallelFloatingArrays() throws Exception {
        when(sellerShippingTimesService.shippingTimes()).thenReturn(new SellerShippingTimesResponse(
                List.of("10-99 orders"), List.of("seller-a"), List.of(2.5)));

        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/sellers/shipping-times"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString()).isEqualTo(
                "{\"bucket\":[\"10-99 orders\"],\"seller_id\":[\"seller-a\"],\"delivery_time\":[2.5]}");
        var json = jsonMapper.readTree(response.getContentAsString());
        JsonContractAssertions.hasExactObjectKeys(json, "bucket", "seller_id", "delivery_time");
        JsonContractAssertions.hasParallelArrayLengths(json, "bucket", "seller_id", "delivery_time");
    }

    @Test
    void categoryWeightsUsesOrderedDynamicKeysAndFilteredFloatArrays() throws Exception {
        var categories = new LinkedHashMap<String, List<Double>>();
        categories.put("cama_mesa_banho", List.of(100.0, 200.0));
        categories.put("beleza_saude", List.of(300.0));
        when(categoryWeightsService.categoryWeights()).thenReturn(new CategoryWeightsResponse(categories));

        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/weights"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString()).isEqualTo(
                "{\"cama_mesa_banho\":[100.0,200.0],\"beleza_saude\":[300.0]}");
        JsonContractAssertions.hasExactObjectKeys(
                jsonMapper.readTree(response.getContentAsString()), "cama_mesa_banho", "beleza_saude");
    }
}
