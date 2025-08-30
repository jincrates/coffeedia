package io.coffeedia;

import io.coffeedia.application.port.repository.BeanRepositoryPort;
import io.coffeedia.application.port.repository.FlavorRepositoryPort;
import io.coffeedia.bootstrap.Application;
import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.model.Flavor;
import io.coffeedia.domain.vo.AccessType;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.BlendType;
import io.coffeedia.domain.vo.Origin;
import io.coffeedia.domain.vo.ProcessType;
import io.coffeedia.domain.vo.RoastLevel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = Application.class
)
public abstract class IntegrationSupportTest {

    @Autowired
    protected Environment env;

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected BeanRepositoryPort beanRepository;

    @Autowired
    protected FlavorRepositoryPort flavorRepository;

    static {
        TestContainerManager.POSTGRES_CONTAINER.start();
        TestContainerManager.REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    private static void registerProperties(DynamicPropertyRegistry registry) {
        TestContainerManager.registerPostgresProperties(registry);
        TestContainerManager.registerRedisProperties(registry);
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

    protected List<Bean> createBeans(int count) {
        List<Flavor> flavors = List.of(
            Flavor.builder().id(1L).name("플레이버1").build(),
            Flavor.builder().id(2L).name("플레이버2").build()
        );

        List<Bean> beans = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Bean bean = Bean.builder()
                .name("조회용 에티오피아 예가체프" + i)
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
                .build();
            beans.add(bean);
        }

        return beanRepository.createAll(beans);
    }
}
