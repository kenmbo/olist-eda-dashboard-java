package com.olist.dashboard.repository;

import java.util.ArrayList;
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

    /** Maps all explicitly named source hour columns without changing weekday or SQL row order. */
    public List<HourlyOrderRow> findHourlyOrderCounts() {
        return sqlQueryExecutor.query(
                "orders/hourly.sql",
                Map.of(),
                (resultSet, rowNumber) -> {
                    var hourlyCounts = new ArrayList<Long>(24);
                    for (int hour = 0; hour < 24; hour++) {
                        hourlyCounts.add(resultSet.getLong(Integer.toString(hour)));
                    }
                    return new HourlyOrderRow(resultSet.getString("day_of_week_name"), hourlyCounts);
                });
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
