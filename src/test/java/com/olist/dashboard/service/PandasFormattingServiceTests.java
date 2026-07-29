package com.olist.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PandasFormattingServiceTests {

    private final PandasFormattingService formatting = new PandasFormattingService();

    @Test
    void roundsWithTheObservedPandasScaledHalfEvenBehavior() {
        assertThat(formatting.roundToTwoDecimals(1.234)).isEqualTo(1.23);
        assertThat(formatting.roundToTwoDecimals(1.125)).isEqualTo(1.12);
        assertThat(formatting.roundToTwoDecimals(1.375)).isEqualTo(1.38);
        assertThat(formatting.roundToTwoDecimals(1.245)).isEqualTo(1.25);
        assertThat(formatting.roundToTwoDecimals(2.675)).isEqualTo(2.68);
        assertThat(formatting.roundToTwoDecimals(-1.125)).isEqualTo(-1.12);
    }

    @Test
    void retainsNullAndNonFiniteNumericValuesRatherThanInventingAJsonValue() {
        assertThat(formatting.roundToTwoDecimals(null)).isNull();
        assertThat(formatting.roundToTwoDecimals(Double.NaN)).isNaN();
        assertThat(formatting.roundToTwoDecimals(Double.POSITIVE_INFINITY)).isEqualTo(Double.POSITIVE_INFINITY);
    }

    @Test
    void matchesCapturedUnderscoreReplacementAndTitleCasingForOriginsAndCities() {
        assertThat(formatting.underscoreToTitleCase("organic_search")).isEqualTo("Organic Search");
        assertThat(formatting.underscoreToTitleCase("other__publicities")).isEqualTo("Other  Publicities");
        assertThat(formatting.underscoreToTitleCase("sao bernardo do campo"))
                .isEqualTo("Sao Bernardo Do Campo");
        assertThat(formatting.underscoreToTitleCase(null)).isNull();
    }

    @Test
    void formatsReviewScoresWithTheLiteralFastApiStarLabel() {
        assertThat(formatting.reviewScoreLabel(1L)).isEqualTo("1 ★");
        assertThat(formatting.reviewScoreLabel(5L)).isEqualTo("5 ★");
        assertThat(formatting.reviewScoreLabel(null)).isNull();
    }
}
