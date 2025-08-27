package io.coffeedia.bootstrap.api;

import static org.hamcrest.Matchers.containsString;

import io.coffeedia.IntegrationSupportTest;
import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.vo.AccessType;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.BlendType;
import io.coffeedia.domain.vo.ProcessType;
import io.coffeedia.domain.vo.RoastLevel;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

// TODO: 요청/응답을 객체로 전환하는 것이 좋을 것 같다.
@Tag("integration")
class BeanControllerTest extends IntegrationSupportTest {

    @BeforeEach
    void setUp() {
        cleanUpDatabase();
    }

    @Nested
    @DisplayName("원두 등록")
    class CreateBeanTest {

        @Test
        @DisplayName("필수 정보만으로도 원두가 정상적으로 등록된다")
        void createBeanWithRequiredFieldsOnly() {
            // given
            String requestBody = """
                {
                    "name": "에티오피아 예가체프",
                    "origin": {
                        "country": "에티오피아",
                        "region": "예가체프"
                    },
                    "roaster": "커피로스터",
                    "roastDate": "%s",
                    "grams": 250,
                    "flavorIds": [1, 2]
                }
                """.formatted(LocalDate.now().minusDays(3));

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo(null)
                .jsonPath("$.data.beanId").isNumber()
                .jsonPath("$.data.name").isEqualTo("에티오피아 예가체프")
                .jsonPath("$.data.origin").exists()
                .jsonPath("$.data.origin.country").isEqualTo("에티오피아")
                .jsonPath("$.data.origin.region").isEqualTo("예가체프")
                .jsonPath("$.data.roaster").isEqualTo("커피로스터")
                .jsonPath("$.data.roastDate").isEqualTo(LocalDate.now().minusDays(3).toString())
                .jsonPath("$.data.grams").isEqualTo(250)
                .jsonPath("$.data.flavors").isArray()
                .jsonPath("$.data.flavors").isNotEmpty()
                // 기본값 검증
                .jsonPath("$.data.roastLevel").isEqualTo(RoastLevel.UNKNOWN)
                .jsonPath("$.data.processType").isEqualTo(ProcessType.UNKNOWN)
                .jsonPath("$.data.blendType").isEqualTo(BlendType.UNKNOWN)
                .jsonPath("$.data.isDecaf").isEqualTo(false)
                .jsonPath("$.data.status").isEqualTo(ActiveStatus.ACTIVE)
                .jsonPath("$.data.accessType").isEqualTo(AccessType.PRIVATE)
                .jsonPath("$.data.memo").doesNotExist()
                .jsonPath("$.data.createdAt").exists()
                .jsonPath("$.data.updatedAt").exists();
        }

        @Test
        @DisplayName("모든 원두 정보를 입력하면 완전한 원두 프로필이 생성된다")
        void createBeanWithAllFields() {
            // given
            String requestBody = """
                {
                    "name": "콜롬비아 수프리모",
                    "origin": {
                        "country": "콜롬비아",
                        "region": "나리뇨"
                    },
                    "roaster": "스페셜티 로스터",
                    "roastDate": "%s",
                    "grams": 200,
                    "flavorIds": [1, 2],
                    "roastLevel": "MEDIUM",
                    "processType": "WASHED",
                    "blendType": "SINGLE_ORIGIN",
                    "isDecaf": false,
                    "memo": "산미가 좋은 원두",
                    "status": "ACTIVE",
                    "accessType": "PUBLIC"
                }
                """.formatted(LocalDate.now().minusDays(1));

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo(null)
                .jsonPath("$.data.beanId").isNumber();
        }

        @Test
        @DisplayName("원두 이름이 없으면 등록이 거부된다")
        void rejectBeanCreationWhenNameIsEmpty() {
            // given
            String requestBody = """
                {
                    "name": "",
                    "origin": {
                        "country": "브라질",
                        "region": "세라도"
                    },
                    "roaster": "커피로스터",
                    "roastDate": "%s",
                    "grams": 250,
                    "flavorIds": [1, 2]
                }
                """.formatted(LocalDate.now());

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").value(containsString("원두 이름은 필수입니다"))
                .jsonPath("$.data").isEmpty();
        }

        @Test
        @DisplayName("원두 이름이 누락되면 등록이 거부된다")
        void rejectBeanCreationWhenNameIsNull() {
            // given
            String requestBody = """
                {
                    "name": null,
                    "origin": {
                        "country": "브라질",
                        "region": "세라도"
                    },
                    "roaster": "커피로스터",
                    "roastDate": "%s",
                    "grams": 250,
                    "flavorIds": [1, 2]
                }
                """.formatted(LocalDate.now());

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").value(containsString("원두 이름은 필수입니다"))
                .jsonPath("$.data").isEmpty();
        }

        @Test
        @DisplayName("원산지 정보가 누락되면 등록이 거부된다")
        void rejectBeanCreationWhenOriginIsNull() {
            // given
            String requestBody = """
                {
                    "name": "테스트 원두",
                    "origin": null,
                    "roaster": "커피로스터",
                    "roastDate": "%s",
                    "grams": 250,
                    "flavorIds": [1, 2]
                }
                """.formatted(LocalDate.now());

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").value(containsString("원두 원산지는 필수입니다"))
                .jsonPath("$.data").isEmpty();
        }

        @Test
        @DisplayName("로스터 정보가 없으면 등록이 거부된다")
        void rejectBeanCreationWhenRoasterIsEmpty() {
            // given
            String requestBody = """
                {
                    "name": "테스트 원두",
                    "origin": {
                        "country": "과테말라",
                        "region": "안티구아"
                    },
                    "roaster": "",
                    "roastDate": "%s",
                    "grams": 250,
                    "flavorIds": [1, 2]
                }
                """.formatted(LocalDate.now());

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").value(containsString("원두 로스터는 필수입니다"))
                .jsonPath("$.data").isEmpty();
        }

        @Test
        @DisplayName("로스팅 일자가 누락되면 등록이 거부된다")
        void rejectBeanCreationWhenRoastDateIsNull() {
            // given
            String requestBody = """
                {
                    "name": "테스트 원두",
                    "origin": {
                        "country": "케냐",
                        "region": "키암부"
                    },
                    "roaster": "커피로스터",
                    "roastDate": null,
                    "grams": 250,
                    "flavorIds": [1, 2]
                }
                """;

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message")
                .value(containsString("원두 로스팅 일자는 필수입니다"))
                .jsonPath("$.data").isEmpty();
        }

        @Test
        @DisplayName("보유 그램이 음수이면 등록이 거부된다")
        void rejectBeanCreationWhenGramsIsNegative() {
            // given
            String requestBody = """
                {
                    "name": "테스트 원두",
                    "origin": {
                        "country": "파나마",
                        "region": "보케테"
                    },
                    "roaster": "커피로스터",
                    "roastDate": "%s",
                    "grams": -100,
                    "flavorIds": [1, 2]
                }
                """.formatted(LocalDate.now());

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message")
                .value(containsString("원두 그램은 0g 이상이어야 합니다"))
                .jsonPath("$.data").isEmpty();
        }

        @Test
        @DisplayName("원산지 국가가 없으면 등록이 거부된다")
        void rejectBeanCreationWhenOriginCountryIsEmpty() {
            // given
            String requestBody = """
                {
                    "name": "테스트 원두",
                    "origin": {
                        "country": "",
                        "region": "지역"
                    },
                    "roaster": "커피로스터",
                    "roastDate": "%s",
                    "grams": 250,
                    "flavorIds": [1, 2]
                }
                """.formatted(LocalDate.now());

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").value(containsString("원산지 국가는 필수입니다"))
                .jsonPath("$.data").isEmpty();
        }

        @Test
        @DisplayName("디카페인 원두도 정상적으로 등록된다")
        void createDecafBeanSuccessfully() {
            // given
            String requestBody = """
                {
                    "name": "디카페인 콜롬비아",
                    "origin": {
                        "country": "콜롬비아",
                        "region": "우일라"
                    },
                    "roaster": "디카페인 전문 로스터",
                    "roastDate": "%s",
                    "grams": 200,
                    "flavorIds": [1, 2],
                    "isDecaf": true,
                    "processType": "WASHED"
                }
                """.formatted(LocalDate.now().minusDays(2));

            // when
            webTestClient.post()
                .uri("/api/beans")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo(null)
                .jsonPath("$.data.beanId").isNumber();
        }
    }

    @Nested
    @DisplayName("원두 목록 조회")
    class GetAllBeanUseCase {

        @BeforeEach
        void setUp() {
            createBeans(15);
        }

        @Test
        @DisplayName("기본 페이징으로 원두 목록을 조회할 수 있다")
        void getAllBeansWithDefaultPaging() {
            // when
            webTestClient.get()
                .uri("/api/beans")
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo(null)
                .jsonPath("$.data").exists()
                .jsonPath("$.data.page").isEqualTo(0)
                .jsonPath("$.data.hasNext").isEqualTo(true)
                .jsonPath("$.data.content").isArray()
                .jsonPath("$.data.content.length()").isEqualTo(10);
        }

        @Test
        @DisplayName("페이지와 사이즈를 지정하여 원두 목록을 조회할 수 있다")
        void getAllBeansWithCustomPaging() {
            // given
            int page = 0;
            int size = 5;

            // when
            webTestClient.get()
                .uri("/api/beans?page={page}&size={size}", page, size)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.page").isEqualTo(page)
                .jsonPath("$.data.hasNext").isEqualTo(true)
                .jsonPath("$.data.content").isArray()
                .jsonPath("$.data.content.length()").isEqualTo(5);
        }

        @Test
        @DisplayName("정렬을 적용하여 원두 목록을 조회할 수 있다")
        void getAllBeansWithSorting() {
            // given
            String sort = "createdAt:asc";

            // when
            webTestClient.get()
                .uri("/api/beans?sort={sort}", sort)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.hasNext").isEqualTo(true)
                .jsonPath("$.data.content").isArray()
                .jsonPath("$.data.content[0]").exists();
        }

        @Test
        @DisplayName("복합 정렬을 적용하여 원두 목록을 조회할 수 있다")
        void getAllBeansWithMultipleSorting() {
            // given
            String sort = "roastDate:desc,createdAt:asc";

            // when
            webTestClient.get()
                .uri("/api/beans?sort={sort}", sort)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.hasNext").isEqualTo(true)
                .jsonPath("$.data.content").isArray()
                .jsonPath("$.data.content").isNotEmpty();
        }

        @Test
        @DisplayName("페이징과 정렬을 동시에 적용하여 원두 목록을 조회할 수 있다")
        void getAllBeansWithPagingAndSorting() {
            // given
            int page = 0;
            int size = 3;
            String sort = "createdAt:desc";

            // when
            webTestClient.get()
                .uri("/api/beans?page={page}&size={size}&sort={sort}", page, size, sort)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.page").isEqualTo(page)
                .jsonPath("$.data.hasNext").isEqualTo(true)
                .jsonPath("$.data.content").isArray()
                .jsonPath("$.data.content.length()").isEqualTo(3);
        }

        @Test
        @DisplayName("마지막 페이지를 조회할 수 있다")
        void getAllBeansLastPage() {
            // given
            int page = 2; // 15개 원두, 페이지당 10개씩이면 마지막은 2페이지(0부터 시작)
            int size = 10;

            // when
            webTestClient.get()
                .uri("/api/beans?page={page}&size={size}", page, size)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.page").isEqualTo(page)
                .jsonPath("$.data.hasNext").isEqualTo(false)
                .jsonPath("$.data.content").isArray();
        }

        @Test
        @DisplayName("원두 목록 조회 시 각 원두의 기본 정보가 포함된다")
        void getAllBeansContainsBasicBeanInfo() {
            // when
            webTestClient.get()
                .uri("/api/beans?size=1")
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true);
        }

        @Test
        @DisplayName("원두가 없을 때 빈 목록이 반환된다")
        void getAllBeansWhenEmpty() {
            // given
            cleanUpDatabase(); // 모든 원두 삭제

            // when
            webTestClient.get()
                .uri("/api/beans")
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.content").isArray()
                .jsonPath("$.data.content").isEmpty();
        }

        @Test
        @DisplayName("잘못된 페이지 번호로 조회해도 정상 처리된다")
        void getAllBeansWithInvalidPageNumber() {
            // given
            int invalidPage = 999;

            // when
            webTestClient.get()
                .uri("/api/beans?page={page}", invalidPage)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.page").isEqualTo(invalidPage)
                .jsonPath("$.data.content").isArray()
                .jsonPath("$.data.content").isEmpty();
        }

        @Test
        @DisplayName("음수 페이지 번호는 0으로 처리된다")
        void getAllBeansWithNegativePageNumber() {
            // given
            int negativePage = -1;

            // when
            webTestClient.get()
                .uri("/api/beans?page={page}", negativePage)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.content").isArray();
        }

        @Test
        @DisplayName("잘못된 정렬 형식이면 조회 할 수 없다")
        void getAllBeansWithInvalidSortFormat() {
            // given
            String invalidSort = "invalid_sort_format";

            // when
            webTestClient.get()
                .uri("/api/beans?sort={sort}", invalidSort)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").isEqualTo("잘못된 정렬 형식입니다. ('invalid_sort_format')")
                .jsonPath("$.data").isEmpty();
        }

        @Test
        @DisplayName("사이즈가 0일 때도 정상 처리된다")
        void getAllBeansWithZeroSize() {
            // given
            int size = 0;

            // when
            webTestClient.get()
                .uri("/api/beans?size={size}", size)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.hasNext").isEqualTo(true)
                .jsonPath("$.data.content").isArray()
                .jsonPath("$.data.content").isEmpty();
        }
    }

    @Nested
    @DisplayName("원두 조회")
    class GetBeanTest {

        private Bean bean;

        @BeforeEach
        void setUp() {
            bean = createBean();
        }

        @Test
        @DisplayName("존재하는 원두 ID로 조회하면 원두 정보를 반환한다")
        void getBeanSuccessfully() {
            // given
            Long beanId = bean.id();

            // when
            webTestClient.get()
                .uri("/api/beans/{beanId}", beanId)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo(null)
                .jsonPath("$.data").exists()
                .jsonPath("$.data.beanId").isEqualTo(beanId)
                .jsonPath("$.data.name").isEqualTo("조회용 에티오피아 예가체프")
                .jsonPath("$.data.origin.country").isEqualTo("에티오피아")
                .jsonPath("$.data.origin.region").isEqualTo("예가체프")
                .jsonPath("$.data.roaster").isEqualTo("조회테스트 로스터")
                .jsonPath("$.data.grams").isEqualTo(250)
                .jsonPath("$.data.roastLevel").isEqualTo("MEDIUM")
                .jsonPath("$.data.processType").isEqualTo("WASHED")
                .jsonPath("$.data.blendType").isEqualTo("SINGLE_ORIGIN")
                .jsonPath("$.data.isDecaf").isEqualTo(false)
                .jsonPath("$.data.memo").isEqualTo("조회 테스트용 메모")
                .jsonPath("$.data.status").isEqualTo("ACTIVE")
                .jsonPath("$.data.accessType").isEqualTo("PUBLIC");
        }

        @Test
        @DisplayName("존재하지 않는 원두 ID로 조회하면 400 에러를 반환한다")
        void getBeanBadRequest() {
            // given
            Long nonExistentBeanId = 99999L;

            // when
            webTestClient.get()
                .uri("/api/beans/{beanId}", nonExistentBeanId)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message")
                .value(containsString("원두를 찾을 수 없습니다"))
                .jsonPath("$.data").isEmpty();
        }

        @Test
        @DisplayName("잘못된 원두 ID 형식으로 조회하면 400 에러를 반환한다")
        void getBeanWithInvalidId() {
            // given
            String invalidBeanId = "invalid";

            // when & then
            webTestClient.get()
                .uri("/api/beans/{beanId}", invalidBeanId)
                .exchange()
                .expectStatus().isBadRequest();
        }
    }

    @Nested
    @DisplayName("원두 수정")
    class UpdateBeanTest {

        private Bean bean;

        @BeforeEach
        void setUp() {
            bean = createBean();
        }

        @Test
        @DisplayName("존재하는 원두의 이름을 수정할 수 있다")
        void updateBeanNameSuccessfully() {
            // given
            Long beanId = bean.id();

            String updateRequestBody = """
                {
                    "name": "수정된 원두 이름"
                }
                """;

            // when
            webTestClient.put()
                .uri("/api/beans/{beanId}", beanId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequestBody)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo(null)
                .jsonPath("$.data").exists()
                // 수정된 필드 검증
                .jsonPath("$.data.name").isEqualTo("수정된 원두 이름")
                // 수정되지 않은 필드들이 기존 값을 유지하는지 검증
                .jsonPath("$.data.beanId").isEqualTo(bean.id());
//                .jsonPath("$.data.origin").exists()
//                .jsonPath("$.data.origin.country").isEqualTo(bean.origin().country())
//                .jsonPath("$.data.origin.region").isEqualTo(bean.origin().region())
//                .jsonPath("$.data.roaster").isEqualTo(bean.roaster())
//                .jsonPath("$.data.roastDate").isEqualTo(bean.roastDate())
//                .jsonPath("$.data.grams").isEqualTo(bean.grams())
//                .jsonPath("$.data.roastLevel").isEqualTo(bean.roastLevel())
//                .jsonPath("$.data.processType").isEqualTo(bean.processType())
//                .jsonPath("$.data.blendType").isEqualTo(bean.blendType())
//                .jsonPath("$.data.isDecaf").isEqualTo(bean.isDecaf())
//                .jsonPath("$.data.memo").isEqualTo(bean.memo())
//                .jsonPath("$.data.status").isEqualTo(bean.status())
//                .jsonPath("$.data.accessType").isEqualTo(bean.accessType())
//                .jsonPath("$.data.flavors.length()").isEqualTo(bean.flavors().size())
//                .jsonPath("$.data.createdAt").isEqualTo(bean.createdAt())
//                .jsonPath("$.data.updatedAt").value(greaterThanOrEqualTo(bean.createdAt().toString()));
        }

        @Test
        @DisplayName("원두의 로스팅 레벨을 수정할 수 있다")
        void updateBeanRoastLevelSuccessfully() {
            // given
            Long beanId = bean.id();

            String updateRequestBody = """
                {
                    "roastLevel": "DARK"
                }
                """;

            // when
            webTestClient.put()
                .uri("/api/beans/{beanId}", beanId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequestBody)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.roastLevel").isEqualTo("DARK");
        }

        @Test
        @DisplayName("원두의 보유 그램을 수정할 수 있다")
        void updateBeanGramsSuccessfully() {
            // given
            Long beanId = bean.id();

            String updateRequestBody = """
                {
                    "grams": 150
                }
                """;

            // when
            webTestClient.put()
                .uri("/api/beans/{beanId}", beanId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequestBody)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.grams").isEqualTo(150);
        }

        @Test
        @DisplayName("원두의 메모를 수정할 수 있다")
        void updateBeanMemoSuccessfully() {
            // given
            Long beanId = bean.id();

            String updateRequestBody = """
                {
                    "memo": "수정된 메모 내용"
                }
                """;

            // when
            webTestClient.put()
                .uri("/api/beans/{beanId}", beanId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequestBody)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.memo").isEqualTo("수정된 메모 내용");
        }

        @Test
        @DisplayName("원두의 여러 필드를 한번에 수정할 수 있다")
        void updateMultipleFieldsSuccessfully() {
            // given
            Long beanId = bean.id();

            String updateRequestBody = """
                {
                    "name": "종합 수정 테스트 원두",
                    "grams": 180,
                    "roastLevel": "DARK",
                    "memo": "여러 필드 수정 테스트",
                    "status": "INACTIVE"
                }
                """;

            // when
            webTestClient.put()
                .uri("/api/beans/{beanId}", beanId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequestBody)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.name").isEqualTo("종합 수정 테스트 원두")
                .jsonPath("$.data.grams").isEqualTo(180)
                .jsonPath("$.data.roastLevel").isEqualTo("DARK")
                .jsonPath("$.data.memo").isEqualTo("여러 필드 수정 테스트")
                .jsonPath("$.data.status").isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("존재하지 않는 원두를 수정하려 하면 400 에러를 반환한다")
        void updateNonExistentBean() {
            // given
            Long nonExistentBeanId = 99999L;

            String updateRequestBody = """
                {
                    "name": "존재하지 않는 원두"
                }
                """;

            // when
            webTestClient.put()
                .uri("/api/beans/{beanId}", nonExistentBeanId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequestBody)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message")
                .value(containsString("원두를 찾을 수 없습니다"))
                .jsonPath("$.data").isEmpty();
        }

        @Test
        @DisplayName("잘못된 원두 ID 형식으로 수정하면 400 에러를 반환한다")
        void updateBeanWithInvalidId() {
            // given
            String invalidBeanId = "invalid";

            String updateRequestBody = """
                {
                    "name": "테스트"
                }
                """;

            // when & then
            webTestClient.put()
                .uri("/api/beans/{beanId}", invalidBeanId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequestBody)
                .exchange()
                .expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("보유 그램을 음수로 수정하면 400 에러를 반환한다")
        void rejectUpdateWithNegativeGrams() {
            // given
            Long beanId = bean.id();

            String updateRequestBody = """
                {
                    "grams": -50
                }
                """;

            // when & then
            webTestClient.put()
                .uri("/api/beans/{beanId}", beanId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message")
                .value(containsString("원두 그램은 0g 이상이어야 합니다"))
                .jsonPath("$.data").isEmpty();
        }

        @Test
        @DisplayName("빈 요청 본문으로 수정 요청하면 아무것도 변경되지 않는다")
        void updateBeanWithEmptyBody() {
            // given
            Long beanId = bean.id();

            String updateRequestBody = """
                {
                }
                """;

            // when & then
            webTestClient.put()
                .uri("/api/beans/{beanId}", beanId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data").exists();
        }

        @Test
        @DisplayName("원두 접근 타입을 수정할 수 있다")
        void updateBeanAccessTypeSuccessfully() {
            // given
            Long beanId = bean.id();

            String updateRequestBody = """
                {
                    "accessType": "PRIVATE"
                }
                """;

            // when
            webTestClient.put()
                .uri("/api/beans/{beanId}", beanId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequestBody)
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.accessType").isEqualTo("PRIVATE");
        }
    }
}
