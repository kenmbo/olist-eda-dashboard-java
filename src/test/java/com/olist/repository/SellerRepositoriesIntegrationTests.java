package com.olist.dashboard.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.olist.dashboard.support.SellerEndpointSqliteFixture;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class SellerRepositoriesIntegrationTests {

    private static final Path TEST_DATABASE = SellerEndpointSqliteFixture.createSeededDatabase();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("olist.database.path", () -> TEST_DATABASE.toString());
        registry.add("olist.cors.origins", () -> "");
    }

    @Autowired
    private SellerPerformanceRepository sellerPerformanceRepository;

    @Autowired
    private SellerDistributionRepository sellerDistributionRepository;

    @Test
    void mapsTheSourcePerformanceJoinMultiplicityThresholdAndFrozenSellerOrdering() {
        List<SellerPerformanceRow> rows = sellerPerformanceRepository.findAll();

        assertThat(rows).extracting(SellerPerformanceRow::sellerId)
                .containsExactly("seller-alpha", "seller-beta");
        assertThat(rows).extracting(SellerPerformanceRow::numOrders)
                .containsExactly(12L, 11L);
        assertThat(rows).extracting(SellerPerformanceRow::totalSales)
                .containsExactly(120.0, 60.5);
        assertThat(rows.getFirst().avgReviewScore())
                .isCloseTo(46.0 / 12.0, offset(1e-12));
        assertThat(rows.get(1).avgReviewScore()).isEqualTo(5.0);
        assertThat(rows).extracting(SellerPerformanceRow::sellerId)
                .doesNotContain("seller-excluded", "seller-no-items");
    }

    @Test
    void mapsAllSourceBucketBoundariesWithTheFrozenBucketOrdering() {
        assertThat(sellerDistributionRepository.findAll()).containsExactly(
                new SellerDistributionRow("1-9 orders", 2L),
                new SellerDistributionRow("10-99 orders", 5L),
                new SellerDistributionRow("100-999 orders", 1L),
                new SellerDistributionRow("1000+ orders", 1L));
    }
}
