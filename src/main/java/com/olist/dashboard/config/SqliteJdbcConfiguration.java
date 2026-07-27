package com.olist.dashboard.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.sqlite.SQLiteDataSource;

/** Creates the single unpooled, read-only SQLite data source used by analytics repositories. */
@Configuration(proxyBeanMethods = false)
public class SqliteJdbcConfiguration {

    /**
     * Uses the validated URI URL from {@link OlistDatabaseProperties}, including {@code mode=ro}.
     *
     * <p>SQLite is a file database and this application serves read-only analytics queries. A
     * plain {@link SQLiteDataSource} intentionally creates connections on demand rather than
     * introducing a connection pool before concurrency requirements are measured.</p>
     */
    @Bean
    public DataSource dataSource(OlistDatabaseProperties databaseProperties) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(databaseProperties.readOnlyJdbcUrl());
        return dataSource;
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
