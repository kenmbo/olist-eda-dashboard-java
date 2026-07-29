package com.olist.dashboard.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/** Executes the source seller review-versus-sales SQL without post-query sorting. */
@Repository
public class SellerReviewSalesRepository {

    private static final String SQL_RESOURCE = "sellers/review-sales.sql";

    private final SqlQueryExecutor sqlQueryExecutor;

    public SellerReviewSalesRepository(SqlQueryExecutor sqlQueryExecutor) {
        this.sqlQueryExecutor = sqlQueryExecutor;
    }

    public List<SellerReviewSalesRow> findReviewSales() {
        return sqlQueryExecutor.query(SQL_RESOURCE, Map.of(), (resultSet, rowNumber) -> new SellerReviewSalesRow(
                resultSet.getString("seller_id"),
                resultSet.getObject("total_sales", Double.class),
                resultSet.getObject("avg_score", Double.class),
                resultSet.getObject("order_count", Long.class)));
    }
}
