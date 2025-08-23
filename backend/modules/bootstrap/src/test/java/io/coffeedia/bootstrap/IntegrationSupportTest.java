package io.coffeedia.bootstrap;

import io.coffeedia.application.port.repository.BeanRepositoryPort;
import io.coffeedia.bootstrap.config.P6SqySqlFormatConfig;
import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.model.Flavor;
import io.coffeedia.domain.vo.AccessType;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.BlendType;
import io.coffeedia.domain.vo.Origin;
import io.coffeedia.domain.vo.ProcessType;
import io.coffeedia.domain.vo.RoastLevel;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@ActiveProfiles("test")
@Import({
    P6SqySqlFormatConfig.class,
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationSupportTest {

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected BeanRepositoryPort beanRepository;

    static {
        TestContainerManager.POSTGRES_CONTAINER.start();
    }

    @DynamicPropertySource
    private static void registerProperties(DynamicPropertyRegistry registry) {
        TestContainerManager.registerPostgresProperties(registry);
    }

    protected void cleanUpDatabase() {
        beanRepository.deleteAll();
    }

    protected Bean createBean() {
        List<Flavor> flavors = List.of(
            Flavor.builder().id(1L).name("플레이버1").build(),
            Flavor.builder().id(2L).name("플레이버2").build()
        );

        return beanRepository.create(
            Bean.builder()
                .name("조회용 에티오피아 예가체프")
                .origin(Origin.builder()
                    .country("에티오피아")
                    .region("예가체프")
                    .build())
                .roaster("조회테스트 로스터")
                .roastDate(LocalDate.now())
                .grams(250)
                .flavors(flavors)
                .roastLevel(RoastLevel.MEDIUM)
                .processType(ProcessType.WASHED)
                .blendType(BlendType.SINGLE_ORIGIN)
                .isDecaf(false)
                .memo("조회 테스트용 메모")
                .status(ActiveStatus.ACTIVE)
                .accessType(AccessType.PUBLIC)
                .build()
        );
    }
}
