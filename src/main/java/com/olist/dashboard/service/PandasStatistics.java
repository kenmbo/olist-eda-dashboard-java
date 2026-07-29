package com.olist.dashboard.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Small deterministic equivalents of the pandas statistics used by the migrated endpoints. */
public final class PandasStatistics {

    private PandasStatistics() {
    }

    /** pandas {@code Series.quantile(q)} default linear interpolation over finite values. */
    public static Double linearQuantile(List<Double> values, double quantile) {
        var sorted = finiteSorted(values);
        if (sorted.isEmpty()) {
            return null;
        }
        if (sorted.size() == 1) {
            return sorted.getFirst();
        }

        double position = (sorted.size() - 1) * quantile;
        int lowerIndex = (int) Math.floor(position);
        int upperIndex = (int) Math.ceil(position);
        double lower = sorted.get(lowerIndex);
        double upper = sorted.get(upperIndex);
        return lower + (position - lowerIndex) * (upper - lower);
    }

    /** pandas {@code Series.std()} default sample standard deviation ({@code ddof=1}). */
    public static Double sampleStandardDeviation(List<Double> values) {
        var finite = finiteSorted(values);
        if (finite.size() < 2) {
            return null;
        }
        double mean = arithmeticMean(finite);
        double squaredDifferences = 0.0;
        for (double value : finite) {
            double difference = value - mean;
            squaredDifferences += difference * difference;
        }
        return Math.sqrt(squaredDifferences / (finite.size() - 1));
    }

    public static Double arithmeticMean(List<Double> values) {
        var finite = finiteSorted(values);
        if (finite.isEmpty()) {
            return null;
        }
        double total = 0.0;
        for (double value : finite) {
            total += value;
        }
        return total / finite.size();
    }

    private static List<Double> finiteSorted(List<Double> values) {
        var sorted = new ArrayList<Double>();
        for (Double value : values) {
            if (value != null && Double.isFinite(value)) {
                sorted.add(value);
            }
        }
        sorted.sort(Comparator.naturalOrder());
        return sorted;
    }
}
