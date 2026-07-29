package com.olist.dashboard.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/** Executes the source ordered review-score distribution query. */
@Repository
public class ReviewAnalyticsRepository {

    private static final String SQL_RESOURCE = "reviews/distribution.sql";

    private final SqlQueryExecutor sqlQueryExecutor;

    public ReviewAnalyticsRepository(SqlQueryExecutor sqlQueryExecutor) {
        this.sqlQueryExecutor = sqlQueryExecutor;
    }

    public List<ReviewDistributionRow> findDistribution() {
        return sqlQueryExecutor.query(SQL_RESOURCE, Map.of(), (resultSet, rowNumber) -> new ReviewDistributionRow(
                resultSet.getObject("review_score", Long.class),
                resultSet.getObject("total_reviews", Long.class)));
    }
}
