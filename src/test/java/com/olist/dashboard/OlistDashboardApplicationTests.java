package com.olist.dashboard;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class OlistDashboardApplicationTests {

    private static final Path TEST_DATABASE = createTestDatabase();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("olist.database.path", () -> TEST_DATABASE.toString());
        registry.add("olist.cors.origins", () -> "");
    }

    @Autowired
    private OlistDashboardApplication application;

    @Test
    void contextLoads() {
        assertThat(application).isNotNull();
    }

    @Test
    void sqliteDriverExecutesReadOnlySmokeQuery() throws Exception {
        String jdbcUrl = "jdbc:sqlite:" + TEST_DATABASE.toUri() + "?mode=ro";
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT 1")) {
            assertThat(resultSet.next()).isTrue();
            assertThat(resultSet.getInt(1)).isEqualTo(1);
        }
    }

    private static Path createTestDatabase() {
        try {
            Path testDirectory = Files.createTempDirectory("olist-dashboard-test-");
            Path database = testDirectory.resolve("smoke.sqlite").toAbsolutePath();
            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + database);
                 Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE smoke_check (id INTEGER PRIMARY KEY)");
            }
            database.toFile().deleteOnExit();
            testDirectory.toFile().deleteOnExit();
            return database;
        } catch (IOException | java.sql.SQLException exception) {
            throw new IllegalStateException("Unable to create an isolated SQLite test database", exception);
        }
    }
}
