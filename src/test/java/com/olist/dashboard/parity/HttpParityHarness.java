package com.olist.dashboard.parity;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** Performs live successful-response parity comparisons using configurable backend base URLs. */
public final class HttpParityHarness {

    private static final String JSON_CONTENT_TYPE = "application/json";

    private final HttpClient httpClient;
    private final JsonMapper jsonMapper;
    private final SemanticJsonComparator comparator;

    public HttpParityHarness() {
        this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build(), JsonMapper.builder().build(),
                new SemanticJsonComparator());
    }

    HttpParityHarness(HttpClient httpClient, JsonMapper jsonMapper, SemanticJsonComparator comparator) {
        this.httpClient = httpClient;
        this.jsonMapper = jsonMapper;
        this.comparator = comparator;
    }

    public List<EndpointParityResult> compareAll(ParityConfiguration configuration)
            throws IOException, InterruptedException {
        var results = new ArrayList<EndpointParityResult>();
        for (String endpoint : configuration.endpoints()) {
            results.add(compareEndpoint(configuration, endpoint));
        }
        return List.copyOf(results);
    }

    private EndpointParityResult compareEndpoint(ParityConfiguration configuration, String endpoint)
            throws IOException, InterruptedException {
        HttpResponse<String> fastApi = get(configuration.fastApiEndpoint(endpoint));
        HttpResponse<String> spring = get(configuration.springEndpoint(endpoint));
        var mismatches = new ArrayList<ParityMismatch>();
        compareTransport(endpoint, "FastAPI", fastApi, mismatches);
        compareTransport(endpoint, "Spring", spring, mismatches);
        if (mismatches.isEmpty()) {
            JsonNode expected = jsonMapper.readTree(fastApi.body());
            JsonNode actual = jsonMapper.readTree(spring.body());
            mismatches.addAll(comparator.compare(expected, actual));
        }
        return new EndpointParityResult(endpoint, mismatches);
    }

    private HttpResponse<String> get(java.net.URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(60))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static void compareTransport(
            String endpoint,
            String backend,
            HttpResponse<String> response,
            List<ParityMismatch> mismatches) {
        if (response.statusCode() != 200) {
            mismatches.add(new ParityMismatch(
                    endpoint,
                    backend + " returned HTTP " + response.statusCode() + " instead of 200"));
        }
        String contentType = response.headers().firstValue("Content-Type").orElse("");
        if (!JSON_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
            mismatches.add(new ParityMismatch(
                    endpoint,
                    backend + " returned Content-Type '" + contentType + "' instead of application/json"));
        }
    }
}
