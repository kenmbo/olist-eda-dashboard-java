package com.olist.dashboard.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Set;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class OlistDatabasePropertiesTests {

    @TempDir
    Path temporaryDirectory;

    @Test
    void rejectsBlankAndRelativePathsBeforeTheyCanDependOnTheWorkingDirectory() {
        assertThatThrownBy(() -> new OlistDatabaseProperties(" ").requiredDatabasePath())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("OLIST_DB_PATH");

        assertThatThrownBy(() -> new OlistDatabaseProperties("olist.sqlite").requiredDatabasePath())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("absolute path");
    }

    @Test
    void rejectsMissingAndNonRegularPathsWithoutCreatingAFile() {
        Path missingDatabase = temporaryDirectory.resolve("missing.sqlite").toAbsolutePath();

        assertThatThrownBy(() -> new OlistDatabaseProperties(missingDatabase.toString()).requiredDatabasePath())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("existing regular file");
        assertThat(Files.exists(missingDatabase)).isFalse();

        assertThatThrownBy(() -> new OlistDatabaseProperties(temporaryDirectory.toString()).requiredDatabasePath())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("existing regular file");
    }

    @Test
    void readOnlyUriCannotCreateAMissingDatabase() {
        Path missingDatabase = temporaryDirectory.resolve("read-only-missing.sqlite").toAbsolutePath();
        String jdbcUrl = "jdbc:sqlite:" + missingDatabase.toUri() + "?mode=ro";

        assertThatThrownBy(() -> {
            try (Connection ignored = DriverManager.getConnection(jdbcUrl)) {
                // Opening must fail because mode=ro cannot create the absent file.
            }
        }).isInstanceOf(java.sql.SQLException.class);

        assertThat(Files.exists(missingDatabase)).isFalse();
    }

    @Test
    void rejectsUnreadableDatabaseFilesWhenThePlatformCanRepresentThatState() throws Exception {
        Path unreadableDatabase = temporaryDirectory.resolve("unreadable.sqlite").toAbsolutePath();
        Files.createFile(unreadableDatabase);
        Assumptions.assumeTrue(
                Files.getFileStore(unreadableDatabase).supportsFileAttributeView(PosixFileAttributeView.class),
                "POSIX file permissions are required for this check");

        Set<PosixFilePermission> originalPermissions = Files.getPosixFilePermissions(unreadableDatabase);
        try {
            Files.setPosixFilePermissions(unreadableDatabase, Set.of());
            Assumptions.assumeFalse(
                    Files.isReadable(unreadableDatabase),
                    "the current user can still read a POSIX mode 000 file");

            assertThatThrownBy(() -> new OlistDatabaseProperties(unreadableDatabase.toString()).requiredDatabasePath())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("readable file");
        } finally {
            Files.setPosixFilePermissions(unreadableDatabase, originalPermissions);
        }
    }
}
