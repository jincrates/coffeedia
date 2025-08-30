package io.coffeedia;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public final class TestContainerManager {

    private TestContainerManager() {
    }

    // image
    private static final String POSTGRES_IMAGE = "postgres:14";
    private static final String REDIS_IMAGE = "redis:7.0";

    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
        new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    public static final GenericContainer<?> REDIS_CONTAINER =
        new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
            .withExposedPorts(6379)
            .withReuse(true);

    static void registerPostgresProperties(DynamicPropertyRegistry registry) {
        // main
        registry.add("spring.datasource.main.hikari.jdbc-url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.main.hikari.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.main.hikari.password", POSTGRES_CONTAINER::getPassword);
        // replica
        registry.add("spring.datasource.replica.hikari.jdbc-url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.replica.hikari.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.replica.hikari.password", POSTGRES_CONTAINER::getPassword);

        // redis
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
    }
}
