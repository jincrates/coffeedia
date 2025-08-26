package io.coffeedia;

import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@Tag("integration")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationSupportTest {

    @Autowired
    protected Environment env;

    @Autowired
    protected WebTestClient webTestClient;

    static {
        TestContainerManager.POSTGRES_CONTAINER.start();
    }

    @DynamicPropertySource
    private static void registerProperties(DynamicPropertyRegistry registry) {
        TestContainerManager.registerPostgresProperties(registry);
    }
}
