package com.olist.dashboard.config;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Environment-backed configuration for the required SQLite database file. */
@ConfigurationProperties(prefix = "olist.database")
public record OlistDatabaseProperties(String path) {

    /**
     * Resolves and validates the configured database before a SQLite JDBC URL is created.
     *
     * <p>SQLite will otherwise create a new empty file for a missing plain JDBC path. Requiring an
     * absolute, readable regular file keeps application startup independent of the process working
     * directory and prevents that failure mode.</p>
     */
    public Path requiredDatabasePath() {
        if (path == null || path.isBlank()) {
            throw configurationError("must be configured with an absolute path to a readable SQLite database file");
        }

        final Path configuredPath;
        try {
            configuredPath = Path.of(path);
        } catch (InvalidPathException exception) {
            throw configurationError("contains an invalid filesystem path", exception);
        }

        if (!configuredPath.isAbsolute()) {
            throw configurationError("must be an absolute path; relative paths would depend on the process working directory");
        }

        Path normalizedPath = configuredPath.normalize();
        if (!Files.isRegularFile(normalizedPath)) {
            throw configurationError("must name an existing regular file: " + normalizedPath);
        }
        if (!Files.isReadable(normalizedPath)) {
            throw configurationError("must name a readable file: " + normalizedPath);
        }
        return normalizedPath;
    }

    /** Builds a SQLite URI URL that instructs the driver to open the validated file read-only. */
    public String readOnlyJdbcUrl() {
        return "jdbc:sqlite:" + requiredDatabasePath().toUri() + "?mode=ro";
    }

    private static IllegalStateException configurationError(String detail) {
        return new IllegalStateException("OLIST_DB_PATH " + detail);
    }

    private static IllegalStateException configurationError(String detail, Exception cause) {
        return new IllegalStateException("OLIST_DB_PATH " + detail, cause);
    }
}
