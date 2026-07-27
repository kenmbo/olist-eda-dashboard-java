package com.olist.dashboard.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OlistCorsPropertiesTests {

    @Test
    void emptyOriginsMatchTheSourceEmptyListBehavior() {
        assertThat(new OlistCorsProperties(null).allowedOrigins()).isEmpty();
        assertThat(new OlistCorsProperties("   ").allowedOrigins()).isEmpty();
        assertThat(new OlistCorsProperties(",, ,").allowedOrigins()).isEmpty();
    }

    @Test
    void originsAreTrimmedAndEmptyEntriesAreDiscardedInSourceOrder() {
        assertThat(new OlistCorsProperties(" https://one.example, ,https://two.example, https://one.example ").allowedOrigins())
                .containsExactly("https://one.example", "https://two.example", "https://one.example");
    }
}
