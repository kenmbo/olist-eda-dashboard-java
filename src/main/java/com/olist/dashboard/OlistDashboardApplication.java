package com.olist.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

/**
 * Application entry point for the incremental FastAPI-to-Spring migration.
 *
 * <p>Milestone 1 intentionally verifies the configured SQLite database with a direct read-only
 * JDBC connection. DataSource and repository infrastructure begins in Milestone 2.</p>
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ConfigurationPropertiesScan
public class OlistDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(OlistDashboardApplication.class, args);
    }
}
