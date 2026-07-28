package com.olist.dashboard.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/** Executes the source-equivalent query for shipping stages grouped by a fixed city list. */
@Repository
public class ShippingAnalyticsRepository {

    private static final String STAGES_BY_CITY_SQL = "shipping/stages-by-city.sql";

    private final SqlQueryExecutor sqlQueryExecutor;

    public ShippingAnalyticsRepository(SqlQueryExecutor sqlQueryExecutor) {
        this.sqlQueryExecutor = sqlQueryExecutor;
    }

    public List<ShippingStageByCityRow> findStagesByCity() {
        return sqlQueryExecutor.query(STAGES_BY_CITY_SQL, Map.of(), (resultSet, rowNumber) ->
                new ShippingStageByCityRow(
                        resultSet.getString("city"),
                        nullableDouble(resultSet, "approved"),
                        nullableDouble(resultSet, "delivered_to_carrier"),
                        nullableDouble(resultSet, "delivered_to_customer"),
                        nullableDouble(resultSet, "estimated_delivery")));
    }

    private static Double nullableDouble(ResultSet resultSet, String column) throws SQLException {
        double value = resultSet.getDouble(column);
        return resultSet.wasNull() ? null : value;
    }
}
