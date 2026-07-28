package com.olist.dashboard.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.sqlite.SQLiteDataSource;

/** Creates the isolated SQLite schema used only by shipping and customer CLV repository tests. */
public final class ShippingCustomersSqliteFixture {

    private ShippingCustomersSqliteFixture() {
    }

    public static Path createSeededDatabase() {
        try {
            Path directory = Files.createTempDirectory("olist-dashboard-shipping-customers-");
            Path database = directory.resolve("shipping-customers-fixture.sqlite").toAbsolutePath();
            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl("jdbc:sqlite:" + database);
            new ResourceDatabasePopulator(
                    new ClassPathResource("sql/test-fixtures/shipping-customers/schema.sql"),
                    new ClassPathResource("sql/test-fixtures/shipping-customers/data.sql"))
                    .execute(dataSource);
            database.toFile().deleteOnExit();
            directory.toFile().deleteOnExit();
            return database;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create the shipping/customers SQLite test fixture", exception);
        }
    }
}
