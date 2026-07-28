package com.olist.dashboard.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/** Reads the direct seller-performance SQL contract. */
@Repository
public class SellerPerformanceRepository {

    private static final String SQL_RESOURCE = "sellers/performance.sql";

    private final SqlQueryExecutor sqlQueryExecutor;

    public SellerPerformanceRepository(SqlQueryExecutor sqlQueryExecutor) {
        this.sqlQueryExecutor = sqlQueryExecutor;
    }

    public List<SellerPerformanceRow> findAll() {
        return sqlQueryExecutor.query(SQL_RESOURCE, Map.of(), this::mapRow);
    }

    private SellerPerformanceRow mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        return new SellerPerformanceRow(
                resultSet.getString("seller_id"),
                resultSet.getObject("avg_review_score", Double.class),
                resultSet.getObject("total_sales", Double.class),
                resultSet.getLong("num_orders"));
    }
}
