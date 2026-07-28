package com.olist.dashboard.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/** Executes the source-equivalent customer lifetime value geographic aggregate query. */
@Repository
public class CustomerAnalyticsRepository {

    private static final String CLV_MAP_SQL = "customers/clv-map.sql";

    private final SqlQueryExecutor sqlQueryExecutor;

    public CustomerAnalyticsRepository(SqlQueryExecutor sqlQueryExecutor) {
        this.sqlQueryExecutor = sqlQueryExecutor;
    }

    public List<CustomerClvMapRow> findClvMapRows() {
        return sqlQueryExecutor.query(CLV_MAP_SQL, Map.of(), (resultSet, rowNumber) ->
                new CustomerClvMapRow(
                        nullableInteger(resultSet, "zip_prefix"),
                        nullableDouble(resultSet, "avg_CLV"),
                        nullableLong(resultSet, "customer_count"),
                        nullableDouble(resultSet, "latitude"),
                        nullableDouble(resultSet, "longitude")));
    }

    private static Integer nullableInteger(ResultSet resultSet, String column) throws SQLException {
        int value = resultSet.getInt(column);
        return resultSet.wasNull() ? null : value;
    }

    private static Long nullableLong(ResultSet resultSet, String column) throws SQLException {
        long value = resultSet.getLong(column);
        return resultSet.wasNull() ? null : value;
    }

    private static Double nullableDouble(ResultSet resultSet, String column) throws SQLException {
        double value = resultSet.getDouble(column);
        return resultSet.wasNull() ? null : value;
    }
}
