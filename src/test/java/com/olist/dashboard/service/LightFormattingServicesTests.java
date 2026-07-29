package com.olist.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.olist.dashboard.repository.DeliveryAnalyticsRepository;
import com.olist.dashboard.repository.DeliveryStageRow;
import com.olist.dashboard.repository.LeadAnalyticsRepository;
import com.olist.dashboard.repository.LeadConversionRow;
import com.olist.dashboard.repository.LeadOriginRow;
import com.olist.dashboard.repository.ReviewAnalyticsRepository;
import com.olist.dashboard.repository.ReviewDistributionRow;
import com.olist.dashboard.repository.SellerReviewSalesRepository;
import com.olist.dashboard.repository.SellerReviewSalesRow;

class LightFormattingServicesTests {

    private final PandasFormattingService formatting = new PandasFormattingService();

    @Test
    void sellerServiceRoundsValuesAndRetainsParallelNulls() {
        var repository = mock(SellerReviewSalesRepository.class);
        when(repository.findReviewSales()).thenReturn(List.of(
                new SellerReviewSalesRow("seller-a", 1.245, 1.125, 6L),
                new SellerReviewSalesRow("seller-b", null, null, null)));

        var response = new SellerReviewSalesService(repository, formatting).reviewSales();

        assertThat(response.sellerIds()).containsExactly("seller-a", "seller-b");
        assertThat(response.totalSales()).containsExactly(1.25, null);
        assertThat(response.averageScores()).containsExactly(1.12, null);
        assertThat(response.orderCounts()).containsExactly(6L, null);
    }

    @Test
    void leadReviewAndDeliveryServicesPreserveRowsAndReturnEmptyArraysForEmptyResults() {
        var leadRepository = mock(LeadAnalyticsRepository.class);
        var reviewRepository = mock(ReviewAnalyticsRepository.class);
        var deliveryRepository = mock(DeliveryAnalyticsRepository.class);
        when(leadRepository.findConversions()).thenReturn(List.of(new LeadConversionRow("direct_traffic", 2L, 1L, 50.0)));
        when(leadRepository.findOrigins()).thenReturn(List.of(new LeadOriginRow(null, 0L)));
        when(reviewRepository.findDistribution()).thenReturn(List.of(new ReviewDistributionRow(1L, 3L)));
        when(deliveryRepository.findStages()).thenReturn(List.of(new DeliveryStageRow("sao paulo", 1.0, 2.0, 3.0)));

        var leads = new LeadAnalyticsService(leadRepository, formatting);
        var reviews = new ReviewAnalyticsService(reviewRepository, formatting);
        var delivery = new DeliveryAnalyticsService(deliveryRepository, formatting);

        assertThat(leads.conversions().origins()).containsExactly("Direct Traffic");
        assertThat(leads.origins().origins()).containsExactly((String) null);
        assertThat(reviews.distribution().scores()).containsExactly("1 ★");
        assertThat(delivery.stages().cities()).containsExactly("Sao Paulo");

        when(leadRepository.findConversions()).thenReturn(List.of());
        when(leadRepository.findOrigins()).thenReturn(List.of());
        when(reviewRepository.findDistribution()).thenReturn(List.of());
        when(deliveryRepository.findStages()).thenReturn(List.of());

        assertThat(leads.conversions().origins()).isEmpty();
        assertThat(leads.origins().leads()).isEmpty();
        assertThat(reviews.distribution().counts()).isEmpty();
        assertThat(delivery.stages().transitDays()).isEmpty();
    }
}
