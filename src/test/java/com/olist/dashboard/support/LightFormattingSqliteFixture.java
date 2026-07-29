package com.olist.dashboard.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.sqlite.SQLiteDataSource;

/** Creates the disposable source-shaped SQLite fixture for Milestone 4 repository tests. */
public final class LightFormattingSqliteFixture {

    private LightFormattingSqliteFixture() {
    }

    public static Path createSeededDatabase() {
        try {
            Path directory = Files.createTempDirectory("olist-light-formatting-");
            Path database = directory.resolve("light-formatting-fixture.sqlite").toAbsolutePath();
            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl("jdbc:sqlite:" + database);
            new ResourceDatabasePopulator(
                    new ClassPathResource("sql/test-fixtures/light-formatting/schema.sql"),
                    new ClassPathResource("sql/test-fixtures/light-formatting/data.sql"))
                    .execute(dataSource);
            database.toFile().deleteOnExit();
            directory.toFile().deleteOnExit();
            return database;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create the light-formatting SQLite fixture", exception);
        }
    }
}
