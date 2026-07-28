package com.olist.dashboard.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.sqlite.SQLiteDataSource;

/** Creates a disposable SQLite database containing seller endpoint edge cases. */
public final class SellerEndpointSqliteFixture {

    private SellerEndpointSqliteFixture() {
    }

    public static Path createSeededDatabase() {
        try {
            Path directory = Files.createTempDirectory("olist-dashboard-sellers-");
            Path database = directory.resolve("sellers-fixture.sqlite").toAbsolutePath();
            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl("jdbc:sqlite:" + database);
            new ResourceDatabasePopulator(
                    new ClassPathResource("sql/test-fixtures/sellers/schema.sql"),
                    new ClassPathResource("sql/test-fixtures/sellers/data.sql"))
                    .execute(dataSource);
            database.toFile().deleteOnExit();
            directory.toFile().deleteOnExit();
            return database;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create an isolated seller endpoint fixture", exception);
        }
    }
}
