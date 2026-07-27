package com.olist.dashboard.repository;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.olist.dashboard.error.AnalyticsDataAccessException;

/**
 * Shared read-only query execution for repositories backed by classpath SQL resources.
 *
 * <p>Feature repositories should keep their public methods domain-specific, supply a typed
 * {@link RowMapper}, and use a SQL resource name such as {@code orders/daily.sql}. This component
 * centralizes named-parameter binding and turns driver failures into a stable application
 * exception for the HTTP layer.</p>
 */
@Component
public class SqlQueryExecutor {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SqlResourceLoader sqlResourceLoader;

    public SqlQueryExecutor(
            NamedParameterJdbcTemplate jdbcTemplate,
            SqlResourceLoader sqlResourceLoader) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlResourceLoader = sqlResourceLoader;
    }

    public <T> List<T> query(String sqlResource, Map<String, ?> parameters, RowMapper<T> rowMapper) {
        try {
            return jdbcTemplate.query(sqlResourceLoader.load(sqlResource), parameters, rowMapper);
        } catch (DataAccessException exception) {
            throw new AnalyticsDataAccessException("Failed to execute SQL resource: " + sqlResource, exception);
        }
    }
}
