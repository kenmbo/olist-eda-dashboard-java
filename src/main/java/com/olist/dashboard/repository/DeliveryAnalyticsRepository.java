package com.olist.dashboard.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/** Executes the source delivered-city stage-duration aggregation query. */
@Repository
public class DeliveryAnalyticsRepository {

    private static final String SQL_RESOURCE = "delivery/stages.sql";

    private final SqlQueryExecutor sqlQueryExecutor;

    public DeliveryAnalyticsRepository(SqlQueryExecutor sqlQueryExecutor) {
        this.sqlQueryExecutor = sqlQueryExecutor;
    }

    public List<DeliveryStageRow> findStages() {
        return sqlQueryExecutor.query(SQL_RESOURCE, Map.of(), (resultSet, rowNumber) -> new DeliveryStageRow(
                resultSet.getString("city"),
                resultSet.getObject("approval_days", Double.class),
                resultSet.getObject("carrier_days", Double.class),
                resultSet.getObject("transit_days", Double.class)));
    }
}
