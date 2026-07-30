package com.olist.dashboard.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.olist.dashboard.support.TestSqliteFixture;

/** Exercises Spring MVC's real CORS processing rather than only property parsing. */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "olist.cors.origins=http://localhost:5173")
class OlistCorsIntegrationTests {

    private static final String ALLOWED_ORIGIN = "http://localhost:5173";
    private static final Path TEST_DATABASE = TestSqliteFixture.createSeededDatabase();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("olist.database.path", () -> TEST_DATABASE.toString());
    }

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @LocalServerPort
    private int port;

    @Test
    void acceptsPreflightFromAnExplicitlyConfiguredFrontendOrigin() throws Exception {
        var response = preflight(ALLOWED_ORIGIN);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)).contains(ALLOWED_ORIGIN);
        assertThat(response.headers().firstValue(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)).contains("true");
        assertThat(response.headers().firstValue(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS)).hasValueSatisfying(
                methods -> assertThat(methods).contains("GET"));
    }

    @Test
    void rejectsPreflightFromAnUnconfiguredOriginWithoutGrantingCorsAccess() throws Exception {
        var response = preflight("https://unconfigured.example");

        assertThat(response.statusCode()).isEqualTo(403);
        assertThat(response.headers().firstValue(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)).isEmpty();
    }

    private HttpResponse<String> preflight(String origin) throws java.io.IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/orders/daily"))
                .timeout(Duration.ofSeconds(10))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header(HttpHeaders.ORIGIN, origin)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
