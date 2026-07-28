package com.olist.dashboard.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.sqlite.SQLiteDataSource;

/** Creates the isolated SQLite fixture for direct orders and category-sales endpoint tests. */
public final class OrdersCategoriesSqliteFixture {

    private OrdersCategoriesSqliteFixture() {
    }

    public static Path createSeededDatabase() {
        try {
            Path directory = Files.createTempDirectory("olist-orders-categories-");
            Path database = directory.resolve("orders-categories.sqlite").toAbsolutePath();
            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl("jdbc:sqlite:" + database);
            new ResourceDatabasePopulator(
                    new ClassPathResource("sql/test-fixtures/orders-categories/schema.sql"),
                    new ClassPathResource("sql/test-fixtures/orders-categories/data.sql"))
                    .execute(dataSource);
            database.toFile().deleteOnExit();
            directory.toFile().deleteOnExit();
            return database;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create the isolated orders/categories fixture", exception);
        }
    }
}
