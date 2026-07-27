package com.olist.dashboard.parity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/** Runs only when an operator explicitly supplies two live backend URLs. */
@EnabledIfSystemProperty(named = "parity.enabled", matches = "true")
class ConfiguredParityHarnessTest {

    @Test
    void comparesTheSelectedFrozenEndpointsAgainstLiveBackends() throws Exception {
        var results = new HttpParityHarness().compareAll(ParityConfiguration.fromSystemPropertiesAndEnvironment());

        assertThat(results)
                .as("semantic FastAPI/Spring parity results")
                .allSatisfy(result -> assertThat(result.mismatches()).as(result.endpoint()).isEmpty());
    }
}
