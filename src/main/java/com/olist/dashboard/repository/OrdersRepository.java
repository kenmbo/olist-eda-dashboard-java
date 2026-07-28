package com.olist.dashboard.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/** Reads the direct, column-oriented orders endpoint queries from SQLite. */
@Repository
public class OrdersRepository {

    private final SqlQueryExecutor sqlQueryExecutor;

    public OrdersRepository(SqlQueryExecutor sqlQueryExecutor) {
        this.sqlQueryExecutor = sqlQueryExecutor;
    }

    /** Preserves the source query's SQLite grouping and result order without adding an ORDER BY. */
    public List<OrderDailyRow> findDailyOrderCounts() {
        return sqlQueryExecutor.query(
                "orders/daily.sql",
                Map.of(),
                (resultSet, rowNumber) -> new OrderDailyRow(
                        resultSet.getString("day"),
                        resultSet.getLong("order_count")));
    }

    /** Preserves the source delivered-order filter, aggregation, and result order. */
    public List<OrderCostRow> findDeliveredOrderCosts() {
        return sqlQueryExecutor.query(
                "orders/costs.sql",
                Map.of(),
                (resultSet, rowNumber) -> new OrderCostRow(
                        resultSet.getString("order_id"),
                        resultSet.getDouble("product_cost"),
                        resultSet.getDouble("shipping_cost")));
    }
}
