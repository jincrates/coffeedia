package io.coffeedia;

import static io.coffeedia.common.constant.CommonConstant.USER_ID;

import io.coffeedia.application.port.repository.BeanRepositoryPort;
import io.coffeedia.application.port.repository.EquipmentRepositoryPort;
import io.coffeedia.application.port.repository.RecipeRepositoryPort;
import io.coffeedia.bootstrap.ApiApplication;
import io.coffeedia.bootstrap.api.security.JwtTokenProvider;
import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.model.Equipment;
import io.coffeedia.domain.model.Flavor;
import io.coffeedia.domain.model.Ingredient;
import io.coffeedia.domain.model.Recipe;
import io.coffeedia.domain.model.RecipeStep;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.BlendType;
import io.coffeedia.domain.vo.CategoryType;
import io.coffeedia.domain.vo.EquipmentType;
import io.coffeedia.domain.vo.Origin;
import io.coffeedia.domain.vo.ProcessType;
import io.coffeedia.domain.vo.RoastLevel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@Tag("integration")
@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = ApiApplication.class
)
public abstract class IntegrationSupportTest {

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected BeanRepositoryPort beanRepository;

    @Autowired
    protected EquipmentRepositoryPort equipmentRepository;

    @Autowired
    protected RecipeRepositoryPort recipeRepository;

    // 테스트용 사용자 정보
    protected static final String TEST_USERNAME = "bjorn";
    protected static final List<String> TEST_ROLES = List.of("customer");
    // 관리자 테스트용
    protected static final String TEST_ADMIN_USERNAME = "isabelle";
    protected static final List<String> TEST_ADMIN_ROLES = List.of("customer", "employee");
    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    static {
        TestContainerManager.POSTGRES_CONTAINER.start();
        TestContainerManager.REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    private static void registerProperties(DynamicPropertyRegistry registry) {
        TestContainerManager.registerPostgresProperties(registry);
        TestContainerManager.registerRedisProperties(registry);
    }

    /**
     * 인증된 WebTestClient 요청을 위한 JWT 토큰 생성
     */
    protected String createAccessToken() {
        return jwtTokenProvider.createAccessToken(TEST_USERNAME, TEST_ROLES);
    }

    /**
     * 관리자 권한 JWT 토큰 생성
     */
    protected String createAdminAccessToken() {
        return jwtTokenProvider.createAccessToken(TEST_ADMIN_USERNAME, TEST_ADMIN_ROLES);
    }

    /**
     * 인증 헤더가 포함된 WebTestClient.RequestHeadersSpec 생성
     */
    protected WebTestClient.RequestHeadersSpec<?> authenticatedGet(String uri,
        Object... uriVariables) {
        return webTestClient.get()
            .uri(uri, uriVariables)
            .header("Authorization", "Bearer " + createAccessToken());
    }

    /**
     * 인증 헤더가 포함된 POST 요청
     */
    protected WebTestClient.RequestBodySpec authenticatedPost(String uri, Object... uriVariables) {
        return webTestClient.post()
            .uri(uri, uriVariables)
            .header("Authorization", "Bearer " + createAccessToken());
    }

    /**
     * 인증 헤더가 포함된 PUT 요청
     */
    protected WebTestClient.RequestBodySpec authenticatedPut(String uri, Object... uriVariables) {
        return webTestClient.put()
            .uri(uri, uriVariables)
            .header("Authorization", "Bearer " + createAccessToken());
    }

    /**
     * 인증 헤더가 포함된 DELETE 요청
     */
    protected WebTestClient.RequestHeadersSpec<?> authenticatedDelete(String uri,
        Object... uriVariables) {
        return webTestClient.delete()
            .uri(uri, uriVariables)
            .header("Authorization", "Bearer " + createAccessToken());
    }

    /**
     * 관리자 권한 GET 요청
     */
    protected WebTestClient.RequestHeadersSpec<?> adminGet(String uri, Object... uriVariables) {
        return webTestClient.get()
            .uri(uri, uriVariables)
            .header("Authorization", "Bearer " + createAdminAccessToken());
    }

    /**
     * 관리자 권한 POST 요청
     */
    protected WebTestClient.RequestBodySpec adminPost(String uri, Object... uriVariables) {
        return webTestClient.post()
            .uri(uri, uriVariables)
            .header("Authorization", "Bearer " + createAdminAccessToken());
    }

    /**
     * 관리자 권한 PUT 요청
     */
    protected WebTestClient.RequestBodySpec adminPut(String uri, Object... uriVariables) {
        return webTestClient.put()
            .uri(uri, uriVariables)
            .header("Authorization", "Bearer " + createAdminAccessToken());
    }

    /**
     * 관리자 권한 DELETE 요청
     */
    protected WebTestClient.RequestHeadersSpec<?> adminDelete(String uri, Object... uriVariables) {
        return webTestClient.delete()
            .uri(uri, uriVariables)
            .header("Authorization", "Bearer " + createAdminAccessToken());
    }

    protected void cleanUpBeans() {
        beanRepository.deleteAll();
    }

    protected void cleanUpEquipments() {
        equipmentRepository.deleteAll();
    }

    protected void cleanUpRecipes() {
        recipeRepository.deleteAll();
    }

    protected Bean createBean() {
        List<Flavor> flavors = List.of(
            Flavor.builder().id(1L).name("플레이버1").build(),
            Flavor.builder().id(2L).name("플레이버2").build()
        );

        return beanRepository.create(
            Bean.builder()
                .userId(USER_ID)
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
                .userId(USER_ID)
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
                .build();
            beans.add(bean);
        }

        return beanRepository.createAll(beans);
    }

    protected Equipment createEquipment() {
        return equipmentRepository.save(
            Equipment.builder()
                .userId(USER_ID)
                .type(EquipmentType.GRINDER)
                .name("조회용 바라짜 엔코어")
                .brand("바라짜")
                .status(ActiveStatus.ACTIVE)
                .description("조회 테스트용 그라인더")
                .buyDate(LocalDate.now().minusMonths(6))
                .buyUrl("https://example.com/grinder")
                .build()
        );
    }

    protected List<Equipment> createEquipments(int count) {
        List<Equipment> equipments = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Equipment equipment = Equipment.builder()
                .userId(USER_ID)
                .type(i % 2 == 0 ? EquipmentType.GRINDER : EquipmentType.SCALE)
                .name("조회용 장비" + i)
                .brand("테스트 브랜드" + i)
                .status(ActiveStatus.ACTIVE)
                .description("조회 테스트용 장비 " + i)
                .buyDate(LocalDate.now().minusMonths(i + 1))
                .buyUrl("https://example.com/equipment" + i)
                .build();
            equipments.add(equipmentRepository.save(equipment));
        }
        return equipments;
    }

    protected Recipe createRecipe() {
        Recipe recipe = Recipe.builder()
            .userId(1L)
            .category(CategoryType.HAND_DRIP)
            .title("V60 핸드드립 커피")
            .thumbnailUrl("https://example.com/v60-thumbnail.jpg")
            .description("맛있는 V60 핸드드립 커피 레시피입니다.")
            .serving(1)
            .tags(List.of("핸드드립", "V60", "중급"))
            .ingredients(List.of(
                Ingredient.builder()
                    .name("원두")
                    .amount(BigDecimal.valueOf(20))
                    .unit("g")
                    .buyUrl("https://example.com/beans")
                    .build(),
                Ingredient.builder()
                    .name("물")
                    .amount(BigDecimal.valueOf(300))
                    .unit("ml")
                    .build()
            ))
            .steps(List.of(  // 필수: 비어있으면 안됨
                RecipeStep.builder()
                    .sortOrder(1)
                    .imageUrl("https://example.com/step1.jpg")
                    .description("원두 20g을 중간 굵기로 분쇄합니다.")
                    .build(),
                RecipeStep.builder()
                    .sortOrder(2)
                    .imageUrl("https://example.com/step2.jpg")
                    .description("물을 90도로 끓입니다.")
                    .build(),
                RecipeStep.builder()
                    .sortOrder(3)
                    .description("30초간 뜸들이기를 합니다.")
                    .build()
            ))
            .tips("분쇄 굵기와 물 온도를 조절하여 취향에 맞게 조정하세요.")
            .status(ActiveStatus.ACTIVE)  // 필수: null 불가
            .build();

        return recipeRepository.save(recipe);
    }

    protected List<Recipe> createRecipes(int count) {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Recipe recipe = createRecipe();
            recipes.add(recipe);
        }
        return recipes;
    }
}
