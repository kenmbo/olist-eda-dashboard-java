package com.olist.dashboard.parity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ParityConfigurationTests {

    @Test
    void normalizesConfigurableBaseUrlsAndSupportsEndpointSelection() {
        ParityConfiguration configuration = ParityConfiguration.fromValues(
                "http://127.0.0.1:8000/",
                "https://spring.example:8080/",
                "/api/orders/daily, /api/orders/costs");

        assertThat(configuration.fastApiEndpoint("/api/orders/daily").toString())
                .isEqualTo("http://127.0.0.1:8000/api/orders/daily");
        assertThat(configuration.springEndpoint("/api/orders/costs").toString())
                .isEqualTo("https://spring.example:8080/api/orders/costs");
        assertThat(configuration.endpoints()).containsExactly("/api/orders/daily", "/api/orders/costs");
    }

    @Test
    void rejectsMissingOrNonHttpParityBaseUrls() {
        assertThatThrownBy(() -> ParityConfiguration.fromValues("", "http://localhost:8080", null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PARITY_FASTAPI_BASE_URL");
        assertThatThrownBy(() -> ParityConfiguration.fromValues("file:///tmp/source", "http://localhost:8080", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("http or https");
    }
}
