package com.olist.dashboard.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/** Reads the source fixed-English-category monthly sales query without Java-side reordering. */
@Repository
public class MonthlySalesRepository {

    private final SqlQueryExecutor sqlQueryExecutor;

    public MonthlySalesRepository(SqlQueryExecutor sqlQueryExecutor) {
        this.sqlQueryExecutor = sqlQueryExecutor;
    }

    public List<MonthlySalesRow> findMonthlySales() {
        return sqlQueryExecutor.query("sales/monthly.sql", Map.of(), (resultSet, rowNumber) -> new MonthlySalesRow(
                resultSet.getString("year_month"),
                nullableDouble(resultSet, "health_beauty"),
                nullableDouble(resultSet, "auto"),
                nullableDouble(resultSet, "toys"),
                nullableDouble(resultSet, "electronics"),
                nullableDouble(resultSet, "fashion_shoes")));
    }

    private static Double nullableDouble(ResultSet resultSet, String column) throws SQLException {
        double value = resultSet.getDouble(column);
        return resultSet.wasNull() ? null : value;
    }
}
