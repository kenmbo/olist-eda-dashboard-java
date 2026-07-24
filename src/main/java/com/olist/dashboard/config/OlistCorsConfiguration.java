package com.olist.dashboard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Spring MVC CORS configuration matching the source application's successful configuration rules. */
@Configuration(proxyBeanMethods = false)
public class OlistCorsConfiguration implements WebMvcConfigurer {

    private final OlistCorsProperties corsProperties;

    public OlistCorsConfiguration(OlistCorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsProperties.allowedOrigins().toArray(String[]::new))
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(600);
    }
}
