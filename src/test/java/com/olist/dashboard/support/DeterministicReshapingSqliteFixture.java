package com.olist.dashboard.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.sqlite.SQLiteDataSource;

/** Creates a disposable source-shaped SQLite database for Milestone 5 repository tests. */
public final class DeterministicReshapingSqliteFixture {

    private DeterministicReshapingSqliteFixture() {
    }

    public static Path createSeededDatabase() {
        try {
            Path directory = Files.createTempDirectory("olist-dashboard-reshaping-");
            Path database = directory.resolve("reshaping-fixture.sqlite").toAbsolutePath();
            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl("jdbc:sqlite:" + database);
            new ResourceDatabasePopulator(
                    new ClassPathResource("sql/test-fixtures/deterministic-reshaping/schema.sql"),
                    new ClassPathResource("sql/test-fixtures/deterministic-reshaping/data.sql"))
                    .execute(dataSource);
            database.toFile().deleteOnExit();
            directory.toFile().deleteOnExit();
            return database;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create the deterministic reshaping SQLite fixture", exception);
        }
    }
}
