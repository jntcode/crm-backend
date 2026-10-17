package com.crm.backend.config;

import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Fails application bootstrap with a single clear, non-sensitive error when the
 * production profile is activated without the required database environment
 * variables. Runs before any bean is created, so startup never reaches a
 * datasource with an unresolved JDBC URL.
 */
public class ProdConfigEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final List<String> REQUIRED_KEYS =
            List.of("DB_HOST", "DB_PORT", "DB_NAME", "DB_USERNAME", "DB_PASSWORD");

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        boolean prod = List.of(environment.getActiveProfiles()).contains("prod")
                || "prod".equals(environment.getProperty("SPRING_PROFILES_ACTIVE"))
                || "prod".equals(environment.getProperty("spring.profiles.active"));
        if (!prod) {
            return;
        }

        List<String> missing = REQUIRED_KEYS.stream()
                .filter(key -> {
                    String value = environment.getProperty(key);
                    return value == null || value.isBlank();
                })
                .toList();

        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "The prod profile requires database configuration. Missing environment variable(s): "
                            + String.join(", ", missing)
                            + ". Set them in the deployment environment (see render.yaml).");
        }
    }

    @Override
    public int getOrder() {
        // After ConfigDataEnvironmentPostProcessor (HIGHEST_PRECEDENCE + 10) so
        // application.properties and environment variables have already been loaded.
        return Ordered.HIGHEST_PRECEDENCE + 11;
    }
}
