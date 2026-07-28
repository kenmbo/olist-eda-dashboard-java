package com.olist.dashboard.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/** Reads the direct seller order-volume distribution SQL contract. */
@Repository
public class SellerDistributionRepository {

    private static final String SQL_RESOURCE = "sellers/distribution.sql";

    private final SqlQueryExecutor sqlQueryExecutor;

    public SellerDistributionRepository(SqlQueryExecutor sqlQueryExecutor) {
        this.sqlQueryExecutor = sqlQueryExecutor;
    }

    public List<SellerDistributionRow> findAll() {
        return sqlQueryExecutor.query(SQL_RESOURCE, Map.of(), this::mapRow);
    }

    private SellerDistributionRow mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        return new SellerDistributionRow(
                resultSet.getString("bucket"),
                resultSet.getLong("seller_count"));
    }
}
