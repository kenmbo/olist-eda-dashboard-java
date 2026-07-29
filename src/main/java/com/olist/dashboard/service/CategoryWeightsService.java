package com.olist.dashboard.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.olist.dashboard.dto.CategoryWeightsResponse;
import com.olist.dashboard.repository.CategoryWeightRow;
import com.olist.dashboard.repository.CategoryWeightsRepository;

/** Implements the approved, filtered FastAPI category-weights migration contract. */
@Service
public class CategoryWeightsService {

    private static final double STANDARD_DEVIATION_MULTIPLIER = 0.8;

    private final CategoryWeightsRepository categoryWeightsRepository;

    public CategoryWeightsService(CategoryWeightsRepository categoryWeightsRepository) {
        this.categoryWeightsRepository = categoryWeightsRepository;
    }

    public CategoryWeightsResponse categoryWeights() {
        return filterTopFive(categoryWeightsRepository.findProductWeights());
    }

    /**
     * Mirrors {@code value_counts().nlargest(5)} and the filtered Python implementation.
     * Equal counts retain their raw first-encounter order, just as pandas 3.0.3 does.
     */
    static CategoryWeightsResponse filterTopFive(List<CategoryWeightRow> rows) {
        var rowsByCategory = new LinkedHashMap<String, List<CategoryWeightRow>>();
        for (var row : rows) {
            if (row.category() != null) {
                rowsByCategory.computeIfAbsent(row.category(), ignored -> new ArrayList<>()).add(row);
            }
        }

        var topCategories = new ArrayList<>(rowsByCategory.entrySet());
        // List.sort is stable, so equal counts preserve the LinkedHashMap's first-seen category order.
        topCategories.sort(Comparator.<Map.Entry<String, List<CategoryWeightRow>>>comparingInt(
                        entry -> entry.getValue().size())
                .reversed());

        var result = new LinkedHashMap<String, List<Double>>();
        for (int index = 0; index < Math.min(5, topCategories.size()); index++) {
            var entry = topCategories.get(index);
            result.put(entry.getKey(), filterCategory(entry.getValue()));
        }
        return new CategoryWeightsResponse(result);
    }

    private static List<Double> filterCategory(List<CategoryWeightRow> rows) {
        var values = rows.stream().map(CategoryWeightRow::weight).toList();
        Double mean = PandasStatistics.arithmeticMean(values);
        Double standardDeviation = PandasStatistics.sampleStandardDeviation(values);
        if (mean == null || standardDeviation == null) {
            // pandas comparisons with the one-value NaN standard deviation are false, retaining it.
            return rows.stream().map(CategoryWeightRow::weight).toList();
        }

        double lowerBound = mean - STANDARD_DEVIATION_MULTIPLIER * standardDeviation;
        double upperBound = mean + STANDARD_DEVIATION_MULTIPLIER * standardDeviation;
        return rows.stream()
                .map(CategoryWeightRow::weight)
                .filter(value -> value != null && Double.isFinite(value))
                .filter(value -> value >= lowerBound && value <= upperBound)
                .toList();
    }
}
