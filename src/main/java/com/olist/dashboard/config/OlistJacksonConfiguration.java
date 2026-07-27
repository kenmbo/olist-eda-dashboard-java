package com.olist.dashboard.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.json.JsonWriteFeature;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.cfg.DateTimeFeature;

/**
 * Explicit JSON policy for FastAPI contract preservation.
 *
 * <p>Dates are ISO strings rather than epoch numbers, unannotated Java properties retain lower
 * camel case, {@code BigDecimal} values remain plain JSON numbers, and null properties and
 * collection elements are retained. Endpoint records must use {@code @JsonProperty} whenever
 * a captured source name is not lower camel case, such as {@code avg_CLV}.</p>
 */
@Configuration(proxyBeanMethods = false)
public class OlistJacksonConfiguration {

    @Bean
    JsonMapperBuilderCustomizer olistJsonMapperBuilderCustomizer() {
        return builder -> builder
                .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN)
                .disable(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS)
                .changeDefaultPropertyInclusion(ignored -> JsonInclude.Value.ALL_ALWAYS);
    }
}

