package com.olist.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.olist.dashboard.repository.CategoryWeightRow;
import com.olist.dashboard.repository.CategoryWeightsRepository;
import com.olist.dashboard.repository.HourlyOrderRow;
import com.olist.dashboard.repository.MonthlyCategorySalesRepository;
import com.olist.dashboard.repository.MonthlyCategorySalesRow;
import com.olist.dashboard.repository.MonthlySalesRepository;
import com.olist.dashboard.repository.MonthlySalesRow;
import com.olist.dashboard.repository.OrdersRepository;
import com.olist.dashboard.repository.SellerShippingTimeRow;
import com.olist.dashboard.repository.SellerShippingTimesRepository;

/** Plain-Java transformation coverage for every Milestone 5 pandas-equivalent service. */
class DeterministicReshapingServicesTests {

    @Test
    void hourlyOrdersKeepsSundayFirstStringColumnsAndIntegerZeroCells() {
        var repository = mock(OrdersRepository.class);
        var sunday = java.util.stream.LongStream.range(0, 24).boxed().toList();
        when(repository.findHourlyOrderCounts()).thenReturn(List.of(new HourlyOrderRow("Sun", sunday)));

        var response = new OrdersService(repository).hourlyOrderCounts();

        assertThat(response.index()).containsExactly("Sun");
        assertThat(response.columns()).containsExactlyElementsOf(
                java.util.stream.IntStream.range(0, 24).mapToObj(Integer::toString).toList());
        assertThat(response.data()).containsExactly(sunday);
        assertThat(response.data().getFirst().get(0)).isZero();
    }

    @Test
    void monthlySalesUsesPandasLocalIsoTimestampStringsAndParallelCategoryValues() {
        var repository = mock(MonthlySalesRepository.class);
        when(repository.findMonthlySales()).thenReturn(List.of(
                new MonthlySalesRow("2017-01", 10.0, 20.0, 30.0, 40.0, 50.0),
                new MonthlySalesRow("2017-02", 1.0, 2.0, 3.0, 4.0, 5.0)));

        var response = new MonthlySalesService(repository).monthlySales();

        assertThat(response.yearMonth()).containsExactly("2017-01-01T00:00:00", "2017-02-01T00:00:00");
        assertThat(response.healthBeauty()).containsExactly(10.0, 1.0);
        assertThat(response.fashionShoes()).containsExactly(50.0, 5.0);
    }

    @Test
    void monthlySalesPreservesTheSourceFailureForAMissingSelectedCategoryAggregate() {
        var repository = mock(MonthlySalesRepository.class);
        when(repository.findMonthlySales()).thenReturn(List.of(
                new MonthlySalesRow("2017-02", 1.0, 2.0, 3.0, 4.0, null)));

        assertThatThrownBy(() -> new MonthlySalesService(repository).monthlySales())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("A selected monthly category aggregate is missing");
    }

    @Test
    void monthlyCategoryPivotUsesSalesRankLexicalTiesSortedAxesZeroCellsAndPandasPrecision() {
        var repository = mock(MonthlyCategorySalesRepository.class);
        when(repository.findMonthlyCategorySales()).thenReturn(List.of(
                new MonthlyCategorySalesRow("2017-02", "z", 10.0),
                new MonthlyCategorySalesRow("2017-01", "a", 10.0),
                new MonthlyCategorySalesRow("2017-01", "m", 10.0),
                new MonthlyCategorySalesRow("2017-01", "b", 10.0),
                new MonthlyCategorySalesRow("2017-01", "c", 10.0),
                new MonthlyCategorySalesRow("2017-01", "d", 10.0),
                new MonthlyCategorySalesRow("2017-01", "small", 9.0),
                new MonthlyCategorySalesRow("2017-02", "a", 119391.00999999992)));

        var response = new MonthlyCategorySalesService(repository).monthlyCategorySales();

        assertThat(response.columns()).containsExactly("a", "b", "c", "d", "m");
        assertThat(response.index()).containsExactly("2017-01", "2017-02");
        assertThat(response.data().getFirst()).containsExactly(10.0, 10.0, 10.0, 10.0, 10.0);
        assertThat(response.data().get(1)).containsExactly(119391.0099999999, 0.0, 0.0, 0.0, 0.0);
    }

    @Test
    void sellerIqrFilteringUsesPerBucketLinearQuartilesInclusiveBoundsAndFirstSeenGroups() {
        var repository = mock(SellerShippingTimesRepository.class);
        when(repository.findSellerShippingTimes()).thenReturn(List.of(
                new SellerShippingTimeRow("medium", "m0", 0.0),
                new SellerShippingTimeRow("small", "s-low", -100.0),
                new SellerShippingTimeRow("medium", "m1", 0.0),
                new SellerShippingTimeRow("small", "s0a", 0.0),
                new SellerShippingTimeRow("medium", "m2", 0.0),
                new SellerShippingTimeRow("small", "s0b", 0.0),
                new SellerShippingTimeRow("medium", "m3", 0.0),
                new SellerShippingTimeRow("small", "s0c", 0.0),
                new SellerShippingTimeRow("medium", "m-high", 100.0),
                new SellerShippingTimeRow("small", "s0d", 0.0),
                new SellerShippingTimeRow("bounds", "b0", 0.0),
                new SellerShippingTimeRow("bounds", "b1", 0.0),
                new SellerShippingTimeRow("bounds", "b2", 1.0),
                new SellerShippingTimeRow("bounds", "b5", 5.0),
                new SellerShippingTimeRow("one", "one", 42.0),
                new SellerShippingTimeRow("null", "null", null)));

        var response = new SellerShippingTimesService(repository).shippingTimes();

        assertThat(response.bucket()).containsExactly(
                "medium", "medium", "medium", "medium",
                "small", "small", "small", "small",
                "bounds", "bounds", "bounds", "bounds", "one");
        assertThat(response.sellerId()).doesNotContain("m-high", "s-low", "null");
        assertThat(response.sellerId()).contains("b5", "one");
    }

    @Test
    void categoryWeightsUsesFirstSeenCountTiesAndFiltersBothSidesWithInclusiveBoundaries() {
        var repository = mock(CategoryWeightsRepository.class);
        when(repository.findProductWeights()).thenReturn(List.of(
                new CategoryWeightRow("z-first", 0.0),
                new CategoryWeightRow("a-second", 100.0),
                new CategoryWeightRow("m-third", 100.0),
                new CategoryWeightRow("b-fourth", 100.0),
                new CategoryWeightRow("c-fifth", 100.0),
                new CategoryWeightRow("d-sixth", 100.0),
                new CategoryWeightRow("z-first", 1.0),
                new CategoryWeightRow("z-first", 4.0),
                new CategoryWeightRow("z-first", 8.0),
                new CategoryWeightRow("z-first", 12.0),
                new CategoryWeightRow("a-second", 100.0),
                new CategoryWeightRow("a-second", 100.0),
                new CategoryWeightRow("a-second", 100.0),
                new CategoryWeightRow("m-third", 100.0),
                new CategoryWeightRow("m-third", 100.0),
                new CategoryWeightRow("m-third", 100.0),
                new CategoryWeightRow("b-fourth", 100.0),
                new CategoryWeightRow("b-fourth", 100.0),
                new CategoryWeightRow("b-fourth", 100.0),
                new CategoryWeightRow("c-fifth", 100.0),
                new CategoryWeightRow("c-fifth", 100.0),
                new CategoryWeightRow("c-fifth", 100.0),
                new CategoryWeightRow("d-sixth", 100.0),
                new CategoryWeightRow("d-sixth", 100.0),
                new CategoryWeightRow("d-sixth", 100.0)));

        var response = new CategoryWeightsService(repository).categoryWeights();

        assertThat(response.categories().keySet()).containsExactly("z-first", "a-second", "m-third", "b-fourth", "c-fifth");
        assertThat(response.categories().get("z-first")).containsExactly(1.0, 4.0, 8.0);
        assertThat(response.categories().get("a-second")).containsExactly(100.0, 100.0, 100.0, 100.0);
    }

    @Test
    void categoryWeightsRetainsOneValueGroupsRepeatedValuesAndExactUpperBoundary() {
        var oneValueRepository = mock(CategoryWeightsRepository.class);
        when(oneValueRepository.findProductWeights()).thenReturn(List.of(new CategoryWeightRow("single", 100.0)));
        assertThat(new CategoryWeightsService(oneValueRepository).categoryWeights().categories().get("single"))
                .containsExactly(100.0);

        var upperBoundaryRepository = mock(CategoryWeightsRepository.class);
        when(upperBoundaryRepository.findProductWeights()).thenReturn(List.of(
                new CategoryWeightRow("upper", 0.0),
                new CategoryWeightRow("upper", 4.0),
                new CategoryWeightRow("upper", 8.0),
                new CategoryWeightRow("upper", 11.0),
                new CategoryWeightRow("upper", 12.0)));
        assertThat(new CategoryWeightsService(upperBoundaryRepository).categoryWeights().categories().get("upper"))
                .containsExactly(4.0, 8.0, 11.0);
    }

    @Test
    void statisticsUsePandasLinearQuartilesAndSampleStandardDeviation() {
        assertThat(PandasStatistics.linearQuantile(List.of(0.0, 10.0, 20.0, 30.0), 0.25)).isEqualTo(7.5);
        assertThat(PandasStatistics.linearQuantile(List.of(0.0, 10.0, 20.0, 30.0), 0.75)).isEqualTo(22.5);
        assertThat(PandasStatistics.sampleStandardDeviation(List.of(0.0, 1.0, 4.0, 8.0, 12.0)))
                .isCloseTo(5.0, offset(1e-12));
        assertThat(PandasStatistics.sampleStandardDeviation(List.of(42.0))).isNull();
    }
}
