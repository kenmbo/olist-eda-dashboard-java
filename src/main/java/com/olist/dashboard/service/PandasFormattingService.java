package com.olist.dashboard.service;

import org.springframework.stereotype.Service;

/** Small, captured-contract subset of pandas string and numeric presentation behavior. */
@Service
public class PandasFormattingService {

    private static final double TWO_DECIMAL_SCALE = 100.0;

    /**
     * Matches pandas {@code Series.round(2)} for ordinary finite IEEE-754 doubles.
     *
     * <p>pandas delegates to NumPy's scaled floating-point rounding, including its half-to-even
     * behavior and binary floating-point effects. {@link Math#rint(double)} uses the same
     * half-to-even IEEE-754 rule after scaling.</p>
     */
    public Double roundToTwoDecimals(Double value) {
        if (value == null || !Double.isFinite(value)) {
            return value;
        }
        return Math.rint(value * TWO_DECIMAL_SCALE) / TWO_DECIMAL_SCALE;
    }

    /** Replaces underscores, then applies the captured pandas {@code str.title()} presentation. */
    public String underscoreToTitleCase(String value) {
        if (value == null) {
            return null;
        }

        String replaced = value.replace('_', ' ');
        StringBuilder result = new StringBuilder(replaced.length());
        boolean previousCharacterWasCased = false;
        for (int offset = 0; offset < replaced.length();) {
            int codePoint = replaced.codePointAt(offset);
            if (Character.isLowerCase(codePoint) || Character.isUpperCase(codePoint) || Character.isTitleCase(codePoint)) {
                result.appendCodePoint(previousCharacterWasCased
                        ? Character.toLowerCase(codePoint)
                        : Character.toTitleCase(codePoint));
                previousCharacterWasCased = true;
            } else {
                result.appendCodePoint(codePoint);
                previousCharacterWasCased = false;
            }
            offset += Character.charCount(codePoint);
        }
        return result.toString();
    }

    /** Matches pandas conversion of integer review scores to strings before appending the star. */
    public String reviewScoreLabel(Long reviewScore) {
        return reviewScore == null ? null : reviewScore + " ★";
    }
}
