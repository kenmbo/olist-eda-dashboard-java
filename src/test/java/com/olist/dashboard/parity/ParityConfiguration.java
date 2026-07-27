package com.olist.dashboard.parity;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

/** Environment- and system-property-backed configuration for a live semantic parity run. */
public record ParityConfiguration(URI fastApiBaseUrl, URI springBaseUrl, List<String> endpoints) {

    public static final List<String> FROZEN_ENDPOINTS = List.of(
            "/api/orders/daily",
            "/api/orders/costs",
            "/api/categories/sales",
            "/api/sellers/performance",
            "/api/sellers/distribution",
            "/api/shipping/stages-by-city",
            "/api/customers/clv-map",
            "/api/sellers/review-sales",
            "/api/leads/conversion",
            "/api/leads/origin",
            "/api/reviews/distribution",
            "/api/delivery/stages",
            "/api/orders/hourly",
            "/api/sales/monthly",
            "/api/categories/monthly-sales",
            "/api/sellers/shipping-times",
            "/api/categories/weights");

    public ParityConfiguration {
        fastApiBaseUrl = normalizeBaseUrl(fastApiBaseUrl);
        springBaseUrl = normalizeBaseUrl(springBaseUrl);
        endpoints = List.copyOf(endpoints);
    }

    public static ParityConfiguration fromSystemPropertiesAndEnvironment() {
        return fromValues(
                configuredValue("parity.fastapi-base-url", "PARITY_FASTAPI_BASE_URL"),
                configuredValue("parity.spring-base-url", "PARITY_SPRING_BASE_URL"),
                configuredValue("parity.endpoints", "PARITY_ENDPOINTS"));
    }

    static ParityConfiguration fromValues(String fastApiBaseUrl, String springBaseUrl, String endpointList) {
        if (isBlank(fastApiBaseUrl) || isBlank(springBaseUrl)) {
            throw new IllegalStateException(
                    "Configure both parity.fastapi-base-url/PARITY_FASTAPI_BASE_URL and "
                            + "parity.spring-base-url/PARITY_SPRING_BASE_URL");
        }
        return new ParityConfiguration(
                URI.create(fastApiBaseUrl.trim()),
                URI.create(springBaseUrl.trim()),
                parseEndpoints(endpointList));
    }

    public URI fastApiEndpoint(String endpoint) {
        return endpointUrl(fastApiBaseUrl, endpoint);
    }

    public URI springEndpoint(String endpoint) {
        return endpointUrl(springBaseUrl, endpoint);
    }

    private static URI normalizeBaseUrl(URI baseUrl) {
        if (baseUrl == null || baseUrl.getScheme() == null || baseUrl.getHost() == null
                || !(baseUrl.getScheme().equalsIgnoreCase("http") || baseUrl.getScheme().equalsIgnoreCase("https"))) {
            throw new IllegalArgumentException("Parity base URLs must be absolute http or https URLs");
        }
        String normalized = baseUrl.toString().replaceFirst("/+$", "");
        return URI.create(normalized);
    }

    private static URI endpointUrl(URI baseUrl, String endpoint) {
        if (endpoint == null || !endpoint.startsWith("/")) {
            throw new IllegalArgumentException("Parity endpoint paths must start with '/'");
        }
        return URI.create(baseUrl + endpoint);
    }

    private static List<String> parseEndpoints(String endpointList) {
        if (isBlank(endpointList)) {
            return FROZEN_ENDPOINTS;
        }
        List<String> endpoints = Arrays.stream(endpointList.split(",", -1))
                .map(String::trim)
                .filter(endpoint -> !endpoint.isEmpty())
                .toList();
        if (endpoints.isEmpty() || endpoints.stream().anyMatch(endpoint -> !endpoint.startsWith("/"))) {
            throw new IllegalArgumentException("PARITY_ENDPOINTS values must be comma-separated paths starting with '/'");
        }
        return endpoints;
    }

    private static String configuredValue(String propertyName, String environmentName) {
        String systemProperty = System.getProperty(propertyName);
        return isBlank(systemProperty) ? System.getenv(environmentName) : systemProperty;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
