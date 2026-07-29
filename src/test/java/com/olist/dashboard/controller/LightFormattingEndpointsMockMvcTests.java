package com.olist.dashboard.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.olist.dashboard.dto.DeliveryStagesResponse;
import com.olist.dashboard.dto.LeadConversionResponse;
import com.olist.dashboard.dto.LeadOriginResponse;
import com.olist.dashboard.dto.ReviewDistributionResponse;
import com.olist.dashboard.dto.SellerReviewSalesResponse;
import com.olist.dashboard.service.DeliveryAnalyticsService;
import com.olist.dashboard.service.LeadAnalyticsService;
import com.olist.dashboard.service.ReviewAnalyticsService;
import com.olist.dashboard.service.SellerReviewSalesService;
import com.olist.dashboard.support.JsonContractAssertions;

import tools.jackson.databind.json.JsonMapper;

/** Verifies exact successful HTTP shapes for every Milestone 4 route. */
class LightFormattingEndpointsMockMvcTests {

    private final SellerReviewSalesService sellerReviewSalesService = mock(SellerReviewSalesService.class);
    private final LeadAnalyticsService leadAnalyticsService = mock(LeadAnalyticsService.class);
    private final ReviewAnalyticsService reviewAnalyticsService = mock(ReviewAnalyticsService.class);
    private final DeliveryAnalyticsService deliveryAnalyticsService = mock(DeliveryAnalyticsService.class);
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
            new SellerReviewSalesController(sellerReviewSalesService),
            new LeadsController(leadAnalyticsService),
            new ReviewsController(reviewAnalyticsService),
            new DeliveryController(deliveryAnalyticsService))
            .build();
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Test
    void sellerReviewSalesUsesTheCapturedCustomArrayNamesAndNumericKinds() throws Exception {
        when(sellerReviewSalesService.reviewSales()).thenReturn(new SellerReviewSalesResponse(
                java.util.List.of("seller-a"), java.util.List.of(10.01), java.util.List.of(4.25), java.util.List.of(6L)));

        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/sellers/review-sales"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        var json = jsonMapper.readTree(response.getContentAsString());
        JsonContractAssertions.hasExactObjectKeys(json, "seller_ids", "total_sales", "avg_scores", "order_counts");
        JsonContractAssertions.hasParallelArrayLengths(json, "seller_ids", "total_sales", "avg_scores", "order_counts");
        assertThat(json.get("total_sales").get(0).isFloatingPointNumber()).isTrue();
        assertThat(json.get("order_counts").get(0).isIntegralNumber()).isTrue();
    }

    @Test
    void leadRoutesUseCleanedOriginsAndTheirSeparateCustomShapes() throws Exception {
        when(leadAnalyticsService.conversions()).thenReturn(new LeadConversionResponse(
                java.util.List.of("Organic Search"), java.util.List.of(4L), java.util.List.of(2L), java.util.List.of(50.0)));
        when(leadAnalyticsService.origins()).thenReturn(new LeadOriginResponse(
                java.util.List.of("Paid Search"), java.util.List.of(3L)));

        var conversions = mockMvc.perform(MockMvcRequestBuilders.get("/api/leads/conversion"))
                .andReturn().getResponse();
        var origins = mockMvc.perform(MockMvcRequestBuilders.get("/api/leads/origin"))
                .andReturn().getResponse();

        assertThat(conversions.getStatus()).isEqualTo(200);
        assertThat(conversions.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        JsonContractAssertions.hasExactObjectKeys(
                jsonMapper.readTree(conversions.getContentAsString()),
                "origins", "qualified_leads", "closed_leads", "conversion_rate");
        assertThat(conversions.getContentAsString()).isEqualTo(
                "{\"origins\":[\"Organic Search\"],\"qualified_leads\":[4],\"closed_leads\":[2],\"conversion_rate\":[50.0]}");

        assertThat(origins.getStatus()).isEqualTo(200);
        assertThat(origins.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        JsonContractAssertions.hasExactObjectKeys(jsonMapper.readTree(origins.getContentAsString()), "origins", "leads");
        assertThat(origins.getContentAsString()).isEqualTo("{\"origins\":[\"Paid Search\"],\"leads\":[3]}");
    }

    @Test
    void reviewDistributionRetainsTheLiteralUnicodeStarLabel() throws Exception {
        when(reviewAnalyticsService.distribution()).thenReturn(
                new ReviewDistributionResponse(java.util.List.of("1 ★"), java.util.List.of(7L)));

        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/distribution"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        var json = jsonMapper.readTree(response.getContentAsString());
        JsonContractAssertions.hasExactObjectKeys(json, "scores", "counts");
        assertThat(json.get("scores").get(0).textValue()).isEqualTo("1 ★");
        assertThat(json.get("counts").get(0).isIntegralNumber()).isTrue();
    }

    @Test
    void deliveryStagesUsesTheExactRouteKeyOrderAndFloatingPointArrays() throws Exception {
        when(deliveryAnalyticsService.stages()).thenReturn(new DeliveryStagesResponse(
                java.util.List.of("Sao Paulo"), java.util.List.of(0.5), java.util.List.of(2.0), java.util.List.of(3.5)));

        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/delivery/stages"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        var json = jsonMapper.readTree(response.getContentAsString());
        JsonContractAssertions.hasExactObjectKeys(json, "cities", "approval_days", "carrier_days", "transit_days");
        JsonContractAssertions.hasParallelArrayLengths(json, "cities", "approval_days", "carrier_days", "transit_days");
        assertThat(response.getContentAsString()).isEqualTo(
                "{\"cities\":[\"Sao Paulo\"],\"approval_days\":[0.5],\"carrier_days\":[2.0],\"transit_days\":[3.5]}");
    }
}
