package com.olist.dashboard.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class SqlResourceLoaderTests {

    private final SqlResourceLoader sqlResourceLoader = new SqlResourceLoader();

    @Test
    void loadsAProductionSqlResourceFromTheSameClasspathRootUsedAfterPackaging() {
        assertThat(sqlResourceLoader.load("shared/select-one.sql"))
                .isEqualTo("SELECT 1 AS value;\n");
    }

    @Test
    void loadsUtf8SqlFromTheClasspathWithoutRewritingIt() {
        assertThat(sqlResourceLoader.load("test-fixtures/loader/valid-select.sql"))
                .isEqualTo("-- This comment proves that the loader does not rewrite SQL.\nSELECT 'ol\u00e1' AS greeting;\n");
    }

    @Test
    void identifiesTheMissingClasspathResource() {
        assertThatThrownBy(() -> sqlResourceLoader.load("orders/not-yet-migrated.sql"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("SQL resource is missing from the classpath: sql/orders/not-yet-migrated.sql");
    }

    @Test
    void rejectsPathsOutsideTheDocumentedResourceConvention() {
        assertThatThrownBy(() -> sqlResourceLoader.load("../orders/daily.sql"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("lower-kebab-case");

        assertThatThrownBy(() -> sqlResourceLoader.load("daily.sql"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("lower-kebab-case");
    }
}
