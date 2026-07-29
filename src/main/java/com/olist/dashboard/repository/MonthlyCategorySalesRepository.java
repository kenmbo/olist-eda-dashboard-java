package com.olist.dashboard.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/** Reads raw delivered monthly category aggregates before source-equivalent top-five pivoting. */
@Repository
public class MonthlyCategorySalesRepository {

    private final SqlQueryExecutor sqlQueryExecutor;

    public MonthlyCategorySalesRepository(SqlQueryExecutor sqlQueryExecutor) {
        this.sqlQueryExecutor = sqlQueryExecutor;
    }

    public List<MonthlyCategorySalesRow> findMonthlyCategorySales() {
        return sqlQueryExecutor.query(
                "categories/monthly-sales.sql",
                Map.of(),
                (resultSet, rowNumber) -> new MonthlyCategorySalesRow(
                        resultSet.getString("order_month"),
                        resultSet.getString("category"),
                        nullableDouble(resultSet, "total_sales")));
    }

    private static Double nullableDouble(ResultSet resultSet, String column) throws SQLException {
        double value = resultSet.getDouble(column);
        return resultSet.wasNull() ? null : value;
    }
}
