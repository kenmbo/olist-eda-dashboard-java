package com.olist.dashboard.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/** Reads every source product-weight observation, retaining order-item repetition and SQL row order. */
@Repository
public class CategoryWeightsRepository {

    private final SqlQueryExecutor sqlQueryExecutor;

    public CategoryWeightsRepository(SqlQueryExecutor sqlQueryExecutor) {
        this.sqlQueryExecutor = sqlQueryExecutor;
    }

    public List<CategoryWeightRow> findProductWeights() {
        return sqlQueryExecutor.query(
                "categories/weights.sql",
                Map.of(),
                (resultSet, rowNumber) -> new CategoryWeightRow(
                        resultSet.getString("category"), nullableDouble(resultSet, "weight")));
    }

    private static Double nullableDouble(ResultSet resultSet, String column) throws SQLException {
        double value = resultSet.getDouble(column);
        return resultSet.wasNull() ? null : value;
    }
}
