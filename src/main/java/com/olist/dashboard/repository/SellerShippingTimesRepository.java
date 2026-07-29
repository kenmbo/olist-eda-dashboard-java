package com.olist.dashboard.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/** Reads delivered seller/order-item durations before source-equivalent IQR filtering. */
@Repository
public class SellerShippingTimesRepository {

    private final SqlQueryExecutor sqlQueryExecutor;

    public SellerShippingTimesRepository(SqlQueryExecutor sqlQueryExecutor) {
        this.sqlQueryExecutor = sqlQueryExecutor;
    }

    /** Does not add an ORDER BY: first-seen SQL order is part of the captured source behavior. */
    public List<SellerShippingTimeRow> findSellerShippingTimes() {
        return sqlQueryExecutor.query(
                "sellers/shipping-times.sql",
                Map.of(),
                (resultSet, rowNumber) -> new SellerShippingTimeRow(
                        resultSet.getString("bucket"),
                        resultSet.getString("seller_id"),
                        nullableDouble(resultSet, "delivery_time")));
    }

    private static Double nullableDouble(ResultSet resultSet, String column) throws SQLException {
        double value = resultSet.getDouble(column);
        return resultSet.wasNull() ? null : value;
    }
}
