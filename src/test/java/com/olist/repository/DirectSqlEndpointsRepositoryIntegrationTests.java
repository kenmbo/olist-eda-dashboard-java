package com.olist.dashboard.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.olist.dashboard.support.OrdersCategoriesSqliteFixture;

/** Executes the three direct endpoint repositories against an isolated, seeded SQLite file. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DirectSqlEndpointsRepositoryIntegrationTests {

    private static final Path TEST_DATABASE = OrdersCategoriesSqliteFixture.createSeededDatabase();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("olist.database.path", () -> TEST_DATABASE.toString());
        registry.add("olist.cors.origins", () -> "");
    }

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private CategorySalesRepository categorySalesRepository;

    @Test
    void dailyOrdersPreserveTheSourceDateGroupingAndObservedSqliteOrder() {
        assertThat(ordersRepository.findDailyOrderCounts()).containsExactly(
                new OrderDailyRow("2018-01-01", 3),
                new OrderDailyRow("2018-01-02", 1),
                new OrderDailyRow("2018-01-03", 1));
    }

    @Test
    void orderCostsFilterNonDeliveredOrdersAndRetainUnroundedDoubleSums() {
        assertThat(ordersRepository.findDeliveredOrderCosts()).containsExactly(
                new OrderCostRow("category-order", 21_000.0, 0.0),
                new OrderCostRow("order-a", 30.0, 3.0),
                new OrderCostRow("order-b", 9.0, 0.5),
                new OrderCostRow("order-z", 8.0, 0.8));
    }

    @Test
    void categorySalesUsesTranslatedTopEighteenRowsThenTheLiteralOtherCategoriesAggregate() {
        List<CategorySalesRow> expectedRows = List.of(
                new CategorySalesRow("Category 01", 2_000.0),
                new CategorySalesRow("Category 02", 1_900.0),
                new CategorySalesRow("Category 03", 1_800.0),
                new CategorySalesRow("Category 04", 1_700.0),
                new CategorySalesRow("Category 05", 1_600.0),
                new CategorySalesRow("Category 06", 1_500.0),
                new CategorySalesRow("Category 07", 1_400.0),
                new CategorySalesRow("Category 08", 1_300.0),
                new CategorySalesRow("Category 09", 1_200.0),
                new CategorySalesRow("Category 10", 1_100.0),
                new CategorySalesRow("Category 11", 1_000.0),
                new CategorySalesRow("Category 12", 900.0),
                new CategorySalesRow("Category 13", 800.0),
                new CategorySalesRow("Category 14", 700.0),
                new CategorySalesRow("Category 15", 600.0),
                new CategorySalesRow("Category 16", 500.0),
                new CategorySalesRow("Category 17", 400.0),
                new CategorySalesRow("Category 18", 300.0),
                new CategorySalesRow("Other categories", 300.0));

        assertThat(categorySalesRepository.findCategorySalesSummary()).containsExactlyElementsOf(expectedRows);
    }
}
