package com.olist.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.olist.dashboard.repository.CustomerAnalyticsRepository;
import com.olist.dashboard.repository.CustomerClvMapRow;
import com.olist.dashboard.repository.ShippingAnalyticsRepository;
import com.olist.dashboard.repository.ShippingStageByCityRow;

class ShippingCustomersServiceTests {

    @Test
    void keepsShippingRowOrderAndParallelColumnAlignment() {
        var repository = mock(ShippingAnalyticsRepository.class);
        when(repository.findStagesByCity()).thenReturn(List.of(
                new ShippingStageByCityRow("SALVADOR", 1.5, 2.5, 4.5, 2.5),
                new ShippingStageByCityRow("SAO PAULO", 1.0, 1.0, 2.0, 2.0),
                new ShippingStageByCityRow("CURITIBA", null, null, null, null)));

        var response = new ShippingAnalyticsService(repository).stagesByCity();

        assertThat(response.city()).containsExactly("SALVADOR", "SAO PAULO", "CURITIBA");
        assertThat(response.approved()).containsExactly(1.5, 1.0, null);
        assertThat(response.deliveredToCarrier()).containsExactly(2.5, 1.0, null);
        assertThat(response.deliveredToCustomer()).containsExactly(4.5, 2.0, null);
        assertThat(response.estimatedDelivery()).containsExactly(2.5, 2.0, null);
    }

    @Test
    void keepsClvPropertyValuesInRepositoryOrder() {
        var repository = mock(CustomerAnalyticsRepository.class);
        when(repository.findClvMapRows()).thenReturn(List.of(
                new CustomerClvMapRow(1003, 425.0, 2L, 2.0, 12.0),
                new CustomerClvMapRow(99990, 300.0, 1L, 6.0, 16.0)));

        var response = new CustomerAnalyticsService(repository).clvMap();

        assertThat(response.zipPrefix()).containsExactly(1003, 99990);
        assertThat(response.averageClv()).containsExactly(425.0, 300.0);
        assertThat(response.customerCount()).containsExactly(2L, 1L);
        assertThat(response.latitude()).containsExactly(2.0, 6.0);
        assertThat(response.longitude()).containsExactly(12.0, 16.0);
    }
}
