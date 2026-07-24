package com.olist.dashboard.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** CORS origins using the source API's comma-separated, trim-and-drop-empty behavior. */
@ConfigurationProperties(prefix = "olist.cors")
public record OlistCorsProperties(String origins) {

    public List<String> allowedOrigins() {
        if (origins == null || origins.isBlank()) {
            return List.of();
        }
        return Arrays.stream(origins.split(",", -1))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();
    }
}
