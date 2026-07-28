package com.olist.dashboard.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.olist.dashboard.support.ShippingCustomersSqliteFixture;

/** Verifies both direct-SQL repositories against a disposable, source-shaped SQLite database. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ShippingCustomersRepositoryIntegrationTests {

    private static final Path TEST_DATABASE = ShippingCustomersSqliteFixture.createSeededDatabase();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("olist.database.path", () -> TEST_DATABASE.toString());
        registry.add("olist.cors.origins", () -> "");
    }

    @Autowired
    private ShippingAnalyticsRepository shippingAnalyticsRepository;

    @Autowired
    private CustomerAnalyticsRepository customerAnalyticsRepository;

    @Test
    void mapsTheFixedSourceCitySetAndSourceStageOrdering() {
        assertThat(shippingAnalyticsRepository.findStagesByCity()).containsExactly(
                new ShippingStageByCityRow("SALVADOR", 1.5, 2.5, 4.5, 2.5),
                new ShippingStageByCityRow("SAO PAULO", 1.0, 1.0, 2.0, 2.0),
                new ShippingStageByCityRow("CURITIBA", null, null, null, null));
    }

    @Test
    void mapsClvGeographyWithAveragedCoordinatesAndIntegerColumns() {
        assertThat(customerAnalyticsRepository.findClvMapRows()).containsExactly(
                new CustomerClvMapRow(1003, 425.0, 2L, 2.0, 12.0),
                new CustomerClvMapRow(99990, 300.0, 1L, 6.0, 16.0));
        assertThat(TEST_DATABASE.getParent().getFileName().toString())
                .startsWith("olist-dashboard-shipping-customers-");
    }
}
