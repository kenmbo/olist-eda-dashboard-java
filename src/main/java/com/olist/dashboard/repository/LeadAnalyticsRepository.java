package com.olist.dashboard.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/** Executes the source lead conversion and lead-origin SQL resources. */
@Repository
public class LeadAnalyticsRepository {

    private static final String CONVERSION_SQL_RESOURCE = "leads/conversion.sql";
    private static final String ORIGIN_SQL_RESOURCE = "leads/origin.sql";

    private final SqlQueryExecutor sqlQueryExecutor;

    public LeadAnalyticsRepository(SqlQueryExecutor sqlQueryExecutor) {
        this.sqlQueryExecutor = sqlQueryExecutor;
    }

    public List<LeadConversionRow> findConversions() {
        return sqlQueryExecutor.query(CONVERSION_SQL_RESOURCE, Map.of(), (resultSet, rowNumber) -> new LeadConversionRow(
                resultSet.getString("origin"),
                resultSet.getObject("qualified_leads", Long.class),
                resultSet.getObject("closed_leads", Long.class),
                resultSet.getObject("conversion_rate", Double.class)));
    }

    public List<LeadOriginRow> findOrigins() {
        return sqlQueryExecutor.query(ORIGIN_SQL_RESOURCE, Map.of(), (resultSet, rowNumber) -> new LeadOriginRow(
                resultSet.getString("origin"),
                resultSet.getObject("total_leads", Long.class)));
    }
}
