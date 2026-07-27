package com.olist.dashboard.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.sqlite.SQLiteDataSource;

import com.olist.dashboard.error.AnalyticsDataAccessException;
import com.olist.dashboard.support.TestSqliteFixture;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class SqliteRepositoryIntegrationTests {

    private static final Path TEST_DATABASE = TestSqliteFixture.createSeededDatabase();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("olist.database.path", () -> TEST_DATABASE.toString());
        registry.add("olist.cors.origins", () -> "");
    }

    @Autowired
    private SqlQueryExecutor sqlQueryExecutor;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Test
    void executesNamedSqlAgainstTheIsolatedFixtureWithATypedRowMapper() {
        List<FixtureMetricRow> rows = sqlQueryExecutor.query(
                "test-fixtures/repository/find-metrics.sql",
                Map.of("minimumAmount", 2.0),
                (resultSet, rowNumber) -> new FixtureMetricRow(
                        resultSet.getInt("metric_id"),
                        resultSet.getString("category"),
                        resultSet.getDouble("amount")));

        assertThat(rows).containsExactly(
                new FixtureMetricRow(2, "beta", 2.5),
                new FixtureMetricRow(3, "gamma", 3.75));
        assertThat(TEST_DATABASE.getParent().getFileName().toString()).startsWith("olist-dashboard-fixture-");
        assertThat(dataSource).isInstanceOf(SQLiteDataSource.class);
    }

    @Test
    void productionDataSourceRejectsWritesToTheFixture() {
        assertThatThrownBy(() -> jdbcTemplate.update(
                "INSERT INTO fixture_metrics (metric_id, category, amount) VALUES (:id, :category, :amount)",
                Map.of("id", 4, "category", "should-not-write", "amount", 4.0)))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void wrapsMalformedSqlAsAnAnalyticsDataAccessFailure() {
        assertThatThrownBy(() -> sqlQueryExecutor.query(
                "test-fixtures/repository/malformed-select.sql",
                Map.of(),
                (resultSet, rowNumber) -> resultSet.getInt(1)))
                .isInstanceOf(AnalyticsDataAccessException.class)
                .hasCauseInstanceOf(DataAccessException.class)
                .hasMessageContaining("malformed-select.sql");
    }

    private record FixtureMetricRow(int id, String category, double amount) {
    }
}
