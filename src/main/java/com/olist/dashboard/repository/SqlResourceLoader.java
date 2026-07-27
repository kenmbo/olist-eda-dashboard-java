package com.olist.dashboard.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Loads UTF-8 SQL resources from the application's {@code sql/} classpath directory.
 *
 * <p>Repositories pass a path relative to that directory, such as {@code orders/daily.sql}.
 * The same classpath lookup is used after packaging and during tests; callers must not resolve
 * SQL files through the process working directory.</p>
 */
@Component
public class SqlResourceLoader {

    private static final String SQL_ROOT = "sql/";
    private static final Pattern RESOURCE_NAME = Pattern.compile(
            "[a-z0-9]+(?:-[a-z0-9]+)*(?:/[a-z0-9]+(?:-[a-z0-9]+)*)+\\.sql");

    /**
     * Returns the complete SQL text for a convention-compliant resource path.
     *
     * @param resourceName path relative to {@code src/main/resources/sql/}
     * @return the resource text without trimming or rewriting SQL
     * @throws IllegalArgumentException if the path does not follow the SQL resource convention
     * @throws IllegalStateException if the classpath resource is missing or cannot be read
     */
    public String load(String resourceName) {
        if (resourceName == null || !RESOURCE_NAME.matcher(resourceName).matches()) {
            throw new IllegalArgumentException(
                    "SQL resource name must be a relative lower-kebab-case .sql path, for example orders/daily.sql");
        }

        String classpathLocation = SQL_ROOT + resourceName;
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        if (!resource.exists()) {
            throw new IllegalStateException("SQL resource is missing from the classpath: " + classpathLocation);
        }

        try (var inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read SQL resource from the classpath: " + classpathLocation, exception);
        }
    }
}
