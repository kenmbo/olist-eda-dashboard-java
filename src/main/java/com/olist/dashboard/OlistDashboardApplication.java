package com.olist.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Application entry point for the incremental FastAPI-to-Spring migration.
 *
 * <p>The application uses explicit SQLite configuration rather than a working-directory-derived
 * default. Shared JDBC and contract infrastructure is intentionally endpoint-neutral until a
 * later migration milestone adds API routes.</p>
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class OlistDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(OlistDashboardApplication.class, args);
    }
}
