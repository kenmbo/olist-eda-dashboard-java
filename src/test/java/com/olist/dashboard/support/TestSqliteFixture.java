package com.olist.dashboard.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.sqlite.SQLiteDataSource;

/** Creates disposable, writable SQLite files for repository integration tests only. */
public final class TestSqliteFixture {

    private TestSqliteFixture() {
    }

    public static Path createSeededDatabase() {
        try {
            Path directory = Files.createTempDirectory("olist-dashboard-fixture-");
            Path database = directory.resolve("analytics-fixture.sqlite").toAbsolutePath();
            initialize(database);
            database.toFile().deleteOnExit();
            directory.toFile().deleteOnExit();
            return database;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create an isolated SQLite test fixture", exception);
        }
    }

    private static void initialize(Path database) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + database);
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                new ClassPathResource("sql/test-fixtures/repository/schema.sql"),
                new ClassPathResource("sql/test-fixtures/repository/data.sql"));
        populator.execute(dataSource);
    }
}
