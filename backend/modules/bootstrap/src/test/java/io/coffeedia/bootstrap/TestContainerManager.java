package io.coffeedia.bootstrap;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;

public final class TestContainerManager {

    private TestContainerManager() {
    }

    // image
    private static final String POSTGRES_IMAGE = "postgres:14";

    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
        new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    static void registerPostgresProperties(DynamicPropertyRegistry registry) {
        // main
        registry.add("spring.datasource.main.hikari.jdbc-url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.main.hikari.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.main.hikari.password", POSTGRES_CONTAINER::getPassword);
        // replica
        registry.add("spring.datasource.replica.hikari.jdbc-url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.replica.hikari.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.replica.hikari.password", POSTGRES_CONTAINER::getPassword);
    }
}
