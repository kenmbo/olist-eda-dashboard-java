package com.olist.dashboard.error;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class ApiExceptionHandlerTests {

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new FailingTestController())
            .setControllerAdvice(new ApiExceptionHandler())
            .build();

    @Test
    void returnsANon2xxSanitizedJsonBodyForInternalDataAccessFailures() throws Exception {
        var response = mockMvc.perform(MockMvcRequestBuilders.get("/test/errors/data-access"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString())
                .isEqualTo("{\"error\":\"The analytics query could not be completed.\"}")
                .doesNotContain("SELECT secret_value")
                .doesNotContain("/private/olist.sqlite");
    }

    @RestController
    private static class FailingTestController {

        @GetMapping("/test/errors/data-access")
        String failWithInternalDetail() {
            throw new AnalyticsDataAccessException(
                    "SELECT secret_value FROM private_table at /private/olist.sqlite");
        }
    }
}
