package com.olist.dashboard.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.olist.dashboard.support.JsonContractAssertions;
import com.olist.dashboard.support.SellerEndpointSqliteFixture;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
class SellerEndpointsMockMvcTests {

    private static final Path TEST_DATABASE = SellerEndpointSqliteFixture.createSeededDatabase();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("olist.database.path", () -> TEST_DATABASE.toString());
        registry.add("olist.cors.origins", () -> "");
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void sellerPerformanceReturnsTheFrozenColumnarJsonContract() throws Exception {
        var response = mockMvc.perform(get("/api/sellers/performance"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).startsWith(MediaType.APPLICATION_JSON_VALUE);

        JsonNode json = objectMapper.readTree(response.getContentAsString());
        JsonContractAssertions.hasExactObjectKeys(
                json, "seller_id", "avg_review_score", "total_sales", "num_orders");
        JsonContractAssertions.hasParallelArrayLengths(
                json, "seller_id", "avg_review_score", "total_sales", "num_orders");
        assertThat(json.get("seller_id").toString())
                .isEqualTo("[\"seller-alpha\",\"seller-beta\"]");
        assertThat(json.get("avg_review_score").get(0).asDouble())
                .isCloseTo(46.0 / 12.0, org.assertj.core.data.Offset.offset(1e-12));
        assertThat(json.get("avg_review_score").get(0).isFloatingPointNumber()).isTrue();
        assertThat(json.get("total_sales").toString()).isEqualTo("[120.0,60.5]");
        assertThat(json.get("num_orders").toString()).isEqualTo("[12,11]");
        assertThat(json.get("num_orders").get(0).isIntegralNumber()).isTrue();
    }

    @Test
    void sellerDistributionPreservesTheApprovedSingularBucketContract() throws Exception {
        var response = mockMvc.perform(get("/api/sellers/distribution"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).startsWith(MediaType.APPLICATION_JSON_VALUE);

        JsonNode json = objectMapper.readTree(response.getContentAsString());
        JsonContractAssertions.hasExactObjectKeys(json, "bucket", "seller_count");
        JsonContractAssertions.hasParallelArrayLengths(json, "bucket", "seller_count");
        assertThat(json.get("bucket").toString()).isEqualTo(
                "[\"1-9 orders\",\"10-99 orders\",\"100-999 orders\",\"1000+ orders\"]");
        assertThat(json.get("seller_count").toString()).isEqualTo("[2,5,1,1]");
        assertThat(json.get("seller_count").get(0).isIntegralNumber()).isTrue();
        assertThat(json.get("buckets")).isNull();
    }
}
