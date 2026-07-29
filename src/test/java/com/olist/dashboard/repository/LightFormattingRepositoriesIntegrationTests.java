package com.olist.dashboard.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.olist.dashboard.support.LightFormattingSqliteFixture;

/** Executes all five Milestone 4 SQL resources against an isolated, source-shaped SQLite file. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class LightFormattingRepositoriesIntegrationTests {

    private static final Path TEST_DATABASE = LightFormattingSqliteFixture.createSeededDatabase();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("olist.database.path", () -> TEST_DATABASE.toString());
        registry.add("olist.cors.origins", () -> "");
    }

    @Autowired
    private SellerReviewSalesRepository sellerReviewSalesRepository;

    @Autowired
    private LeadAnalyticsRepository leadAnalyticsRepository;

    @Autowired
    private ReviewAnalyticsRepository reviewAnalyticsRepository;

    @Autowired
    private DeliveryAnalyticsRepository deliveryAnalyticsRepository;

    @Test
    void sellerReviewSalesRetainsTheSourceThresholdAndRawPreRoundingNumbers() {
        var rows = sellerReviewSalesRepository.findReviewSales();

        assertThat(rows).extracting(SellerReviewSalesRow::sellerId)
                .containsExactly("seller-alpha", "seller-beta");
        assertThat(rows).extracting(SellerReviewSalesRow::orderCount).containsExactly(6L, 6L);
        assertThat(rows.getFirst().totalSales()).isCloseTo(60.03, offset(1e-12));
        assertThat(rows.getFirst().averageScore()).isEqualTo(4.0);
        assertThat(rows).extracting(SellerReviewSalesRow::sellerId).doesNotContain("seller-small");
    }

    @Test
    void leadQueriesRespectTheirDifferentSourceFiltersAndOrdering() {
        var conversionRows = leadAnalyticsRepository.findConversions();
        assertThat(conversionRows).extracting(LeadConversionRow::origin)
                .containsExactly("organic_search", "paid_search", "unknown", "other");
        assertThat(conversionRows).extracting(LeadConversionRow::qualifiedLeads)
                .containsExactly(4L, 3L, 2L, 1L);
        assertThat(conversionRows).extracting(LeadConversionRow::closedLeads)
                .containsExactly(2L, 1L, 1L, 1L);
        assertThat(conversionRows.get(1).conversionRate()).isCloseTo(100.0 / 3.0, offset(1e-12));

        assertThat(leadAnalyticsRepository.findOrigins())
                .containsExactly(new LeadOriginRow("paid_search", 3L), new LeadOriginRow("organic_search", 4L));
    }

    @Test
    void reviewDistributionUsesAscendingIntegerScoresAndCountsAllReviewRows() {
        assertThat(reviewAnalyticsRepository.findDistribution()).containsExactly(
                new ReviewDistributionRow(1L, 7L),
                new ReviewDistributionRow(2L, 1L),
                new ReviewDistributionRow(3L, 3L),
                new ReviewDistributionRow(4L, 6L),
                new ReviewDistributionRow(5L, 6L));
    }

    @Test
    void deliveryStagesUsesTopDeliveredCityVolumeThenExcludesIncompleteStages() {
        var rows = deliveryAnalyticsRepository.findStages();

        assertThat(rows).extracting(DeliveryStageRow::city).containsExactly("sao paulo", "rio de janeiro");
        assertThat(rows.getFirst().approvalDays()).isCloseTo(2.5 / 3.0, offset(1e-12));
        assertThat(rows.getFirst().carrierDays()).isEqualTo(2.0);
        assertThat(rows.getFirst().transitDays()).isCloseTo(11.0 / 3.0, offset(1e-12));
        assertThat(rows.get(1)).isEqualTo(new DeliveryStageRow("rio de janeiro", 0.5, 1.0, 2.5));
    }
}
