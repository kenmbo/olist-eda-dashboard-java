package com.olist.dashboard.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/** Reads the source top-category sales query, including its final aggregate row. */
@Repository
public class CategorySalesRepository {

    private final SqlQueryExecutor sqlQueryExecutor;

    public CategorySalesRepository(SqlQueryExecutor sqlQueryExecutor) {
        this.sqlQueryExecutor = sqlQueryExecutor;
    }

    /**
     * Returns the source query's rank-selected translated categories followed by its literal
     * {@code Other categories} aggregate row. No Java sorting or post-processing is applied.
     */
    public List<CategorySalesRow> findCategorySalesSummary() {
        return sqlQueryExecutor.query(
                "categories/sales.sql",
                Map.of(),
                (resultSet, rowNumber) -> new CategorySalesRow(
                        resultSet.getString("category"),
                        resultSet.getDouble("sales")));
    }
}
