package com.olist.dashboard.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.olist.dashboard.error.ApiExceptionHandler;
import com.olist.dashboard.service.CategorySalesService;
import com.olist.dashboard.service.OrdersService;
import com.olist.dashboard.support.JsonContractAssertions;
import com.olist.dashboard.support.OrdersCategoriesSqliteFixture;

import tools.jackson.databind.json.JsonMapper;

/** Verifies exact HTTP and JSON contracts for the direct SQL endpoint group. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DirectSqlEndpointsMockMvcContractTests {

    private static final Path TEST_DATABASE = OrdersCategoriesSqliteFixture.createSeededDatabase();
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("olist.database.path", () -> TEST_DATABASE.toString());
        registry.add("olist.cors.origins", () -> "");
    }

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private CategorySalesService categorySalesService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new OrdersController(ordersService),
                        new CategorySalesController(categorySalesService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void dailyOrdersReturnsTheCapturedColumnarShapeAndIntegerCounts() throws Exception {
        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/daily")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString())
                .isEqualTo("{\"day\":[\"2018-01-01\",\"2018-01-02\",\"2018-01-03\"],\"order_count\":[3,1,1]}");
        var body = JSON_MAPPER.readTree(response.getContentAsString());
        JsonContractAssertions.hasExactObjectKeys(body, "day", "order_count");
        JsonContractAssertions.hasParallelArrayLengths(body, "day", "order_count");
        assertThat(body.get("order_count").get(0).isIntegralNumber()).isTrue();
    }

    @Test
    void orderCostsReturnsColumnarIdsAndFloatingPointSumsInSqliteRowOrder() throws Exception {
        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/costs")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString()).isEqualTo(
                "{\"order_id\":[\"category-order\",\"order-a\",\"order-b\",\"order-z\"],"
                        + "\"product_cost\":[21000.0,30.0,9.0,8.0],\"shipping_cost\":[0.0,3.0,0.5,0.8]}");
        var body = JSON_MAPPER.readTree(response.getContentAsString());
        JsonContractAssertions.hasExactObjectKeys(body, "order_id", "product_cost", "shipping_cost");
        JsonContractAssertions.hasParallelArrayLengths(body, "order_id", "product_cost", "shipping_cost");
        assertThat(body.get("product_cost").get(0).isFloatingPointNumber()).isTrue();
    }

    @Test
    void categorySalesIncludesExactlyOneFinalOtherCategoriesRow() throws Exception {
        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/sales")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        var body = JSON_MAPPER.readTree(response.getContentAsString());
        JsonContractAssertions.hasExactObjectKeys(body, "category", "sales");
        JsonContractAssertions.hasParallelArrayLengths(body, "category", "sales");
        assertThat(body.get("category").size()).isEqualTo(19);
        assertThat(body.get("category").get(0).textValue()).isEqualTo("Category 01");
        assertThat(body.get("category").get(17).textValue()).isEqualTo("Category 18");
        assertThat(body.get("category").get(18).textValue()).isEqualTo("Other categories");
        assertThat(body.get("sales").get(18).doubleValue()).isEqualTo(300.0);
        assertThat(body.get("sales").get(18).isFloatingPointNumber()).isTrue();
    }
}
