package com.olist.dashboard.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.olist.dashboard.dto.MonthlyCategorySalesResponse;
import com.olist.dashboard.repository.MonthlyCategorySalesRepository;
import com.olist.dashboard.repository.MonthlyCategorySalesRow;

/** Reproduces pandas top-five selection, pivot sorting, zero fill, and JSON decimal precision. */
@Service
public class MonthlyCategorySalesService {

    private final MonthlyCategorySalesRepository monthlyCategorySalesRepository;

    public MonthlyCategorySalesService(MonthlyCategorySalesRepository monthlyCategorySalesRepository) {
        this.monthlyCategorySalesRepository = monthlyCategorySalesRepository;
    }

    public MonthlyCategorySalesResponse monthlyCategorySales() {
        return pivot(monthlyCategorySalesRepository.findMonthlyCategorySales());
    }

    static MonthlyCategorySalesResponse pivot(List<MonthlyCategorySalesRow> rows) {
        var totalsByCategory = new TreeMap<String, Double>();
        for (var row : rows) {
            if (row.category() != null) {
                totalsByCategory.merge(row.category(), zeroWhenNull(row.totalSales()), Double::sum);
            }
        }

        // pandas groupby sorts category labels before nlargest; its stable tie behavior is lexical here.
        var rankedCategories = new ArrayList<>(totalsByCategory.entrySet());
        rankedCategories.sort(Comparator.<Map.Entry<String, Double>>comparingDouble(Map.Entry::getValue)
                .reversed()
                .thenComparing(Map.Entry::getKey));
        var selectedCategories = new LinkedHashSet<String>();
        for (int categoryIndex = 0; categoryIndex < Math.min(5, rankedCategories.size()); categoryIndex++) {
            selectedCategories.add(rankedCategories.get(categoryIndex).getKey());
        }

        // pandas pivot independently sorts both the labels and the date-like string index.
        var columns = selectedCategories.stream().sorted().toList();
        var cells = new TreeMap<String, Map<String, Double>>();
        for (var row : rows) {
            if (row.orderMonth() != null && selectedCategories.contains(row.category())) {
                cells.computeIfAbsent(row.orderMonth(), ignored -> new HashMap<>())
                        .put(row.category(), zeroWhenNull(row.totalSales()));
            }
        }

        var index = new ArrayList<>(cells.keySet());
        var data = new ArrayList<List<Double>>(index.size());
        for (String month : index) {
            var rowCells = cells.get(month);
            var matrixRow = new ArrayList<Double>(columns.size());
            for (String category : columns) {
                matrixRow.add(pandasJsonDoublePrecision(rowCells.getOrDefault(category, 0.0)));
            }
            data.add(matrixRow);
        }
        return new MonthlyCategorySalesResponse(columns, index, data);
    }

    /** pandas {@code to_json} defaults to ten decimal places for finite doubles. */
    static double pandasJsonDoublePrecision(double value) {
        if (!Double.isFinite(value)) {
            return value;
        }
        return java.math.BigDecimal.valueOf(value)
                .setScale(10, java.math.RoundingMode.HALF_UP)
                .doubleValue();
    }

    private static double zeroWhenNull(Double value) {
        return value == null ? 0.0 : value;
    }
}
