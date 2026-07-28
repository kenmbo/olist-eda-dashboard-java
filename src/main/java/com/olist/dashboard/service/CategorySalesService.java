package com.olist.dashboard.service;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.olist.dashboard.dto.ColumnarResponse;
import com.olist.dashboard.repository.CategorySalesRepository;

/** Shapes translated category sales rows into the captured columnar contract. */
@Service
public class CategorySalesService {

    private final CategorySalesRepository categorySalesRepository;

    public CategorySalesService(CategorySalesRepository categorySalesRepository) {
        this.categorySalesRepository = categorySalesRepository;
    }

    public ColumnarResponse categorySales() {
        var rows = categorySalesRepository.findCategorySalesSummary();
        var columns = new LinkedHashMap<String, List<?>>();
        columns.put("category", rows.stream().map(row -> row.category()).toList());
        columns.put("sales", rows.stream().map(row -> row.sales()).toList());
        return new ColumnarResponse(columns);
    }
}
