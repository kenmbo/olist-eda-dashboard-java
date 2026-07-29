package com.olist.dashboard.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.olist.dashboard.support.DeterministicReshapingSqliteFixture;

/** Executes every Milestone 5 SQL resource against an isolated writable SQLite fixture. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DeterministicReshapingRepositoriesIntegrationTests {

    private static final Path TEST_DATABASE = DeterministicReshapingSqliteFixture.createSeededDatabase();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("olist.database.path", () -> TEST_DATABASE.toString());
        registry.add("olist.cors.origins", () -> "");
    }

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private MonthlySalesRepository monthlySalesRepository;

    @Autowired
    private MonthlyCategorySalesRepository monthlyCategorySalesRepository;

    @Autowired
    private SellerShippingTimesRepository sellerShippingTimesRepository;

    @Autowired
    private CategoryWeightsRepository categoryWeightsRepository;

    @Test
    void hourlyQueryProvidesSundayFirstRowsTwentyFourStringNamedCountsAndIntegerZeroes() {
        var rows = ordersRepository.findHourlyOrderCounts();

        assertThat(rows).extracting(HourlyOrderRow::dayOfWeekName)
                .containsExactly("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
        assertThat(rows).allSatisfy(row -> assertThat(row.hourlyCounts()).hasSize(24));
        assertThat(rows.getFirst().hourlyCounts().get(0)).isEqualTo(1L);
        assertThat(rows.getFirst().hourlyCounts().get(23)).isZero();
    }

    @Test
    void monthlySalesQueryIncludesCanceledOrdersButExcludesOldAndUntranslatedRows() {
        var rows = monthlySalesRepository.findMonthlySales();

        assertThat(rows).extracting(MonthlySalesRow::yearMonth).containsExactly("2017-01", "2017-02");
        assertThat(rows.getFirst()).isEqualTo(new MonthlySalesRow("2017-01", 10.0, 20.0, 30.0, 40.0, 50.0));
        assertThat(rows.get(1)).isEqualTo(new MonthlySalesRow("2017-02", 1.0, 2.0, 3.0, 4.0, null));
    }

    @Test
    void monthlyCategoryQueryUsesAllDeliveredRawCategoryMonthsAndPreservesSparseMonthRows() {
        var rows = monthlyCategorySalesRepository.findMonthlyCategorySales();

        assertThat(rows).contains(new MonthlyCategorySalesRow("2017-01", "pivot_a", 100.0));
        assertThat(rows).contains(new MonthlyCategorySalesRow("2017-03", "pivot_a", 1.0));
        assertThat(rows).doesNotContain(new MonthlyCategorySalesRow("2017-01", "monthly_health", 10.0));
        assertThat(rows).contains(new MonthlyCategorySalesRow("2016-12", "monthly_health", 999.0));
    }

    @Test
    void sellerShippingQueryPreservesItemRepetitionAndMapsNullJulianDurations() {
        var rows = sellerShippingTimesRepository.findSellerShippingTimes();

        assertThat(rows).filteredOn(row -> row.sellerId().equals("seller-medium"))
                .hasSize(11)
                .extracting(SellerShippingTimeRow::bucket)
                .containsOnly("10-99 orders");
        assertThat(rows).filteredOn(row -> row.sellerId().equals("seller-medium") && row.deliveryTime() == null)
                .hasSize(1);
        assertThat(rows).filteredOn(row -> row.sellerId().equals("seller-small"))
                .containsExactly(new SellerShippingTimeRow("1-9 orders", "seller-small", 2.0));
    }

    @Test
    void categoryWeightQueryPreservesRepeatedOrderItemObservations() {
        var rows = categoryWeightsRepository.findProductWeights();

        assertThat(rows).filteredOn(row -> row.category().equals("weight_edge"))
                .extracting(CategoryWeightRow::weight)
                .containsExactly(0.0, 1.0, 1.0, 4.0, 8.0, 12.0);
    }
}
