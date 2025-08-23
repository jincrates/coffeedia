package io.coffeedia.bootstrap.api;

import io.coffeedia.bootstrap.IntegrationSupportTest;
import io.coffeedia.domain.model.Bean;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@Tag("integration")
class BeanControllerTest extends IntegrationSupportTest {

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
                .jsonPath("$.data.beanId").isNumber();
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
                .jsonPath("$.message").value(org.hamcrest.Matchers.containsString("원두 이름은 필수입니다"))
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
                .jsonPath("$.message").value(org.hamcrest.Matchers.containsString("원두 이름은 필수입니다"))
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
                .jsonPath("$.message").value(org.hamcrest.Matchers.containsString("원두 원산지는 필수입니다"))
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
                .jsonPath("$.message").value(org.hamcrest.Matchers.containsString("원두 로스터는 필수입니다"))
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
                .value(org.hamcrest.Matchers.containsString("원두 로스팅 일자는 필수입니다"))
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
                .value(org.hamcrest.Matchers.containsString("원두 그램은 0g 이상이어야 합니다"))
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
                .jsonPath("$.message").value(org.hamcrest.Matchers.containsString("원산지 국가는 필수입니다"))
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
    @DisplayName("원두 조회")
    class GetBeanTest {

        private Bean bean;

        @BeforeEach
        void setUp() {
            cleanUpDatabase();
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
                .value(org.hamcrest.Matchers.containsString("원두를 찾을 수 없습니다"))
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
            cleanUpDatabase();
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
                .jsonPath("$.data.beanId").isEqualTo(beanId)
                .jsonPath("$.data.name").isEqualTo("수정된 원두 이름");
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
                .value(org.hamcrest.Matchers.containsString("원두를 찾을 수 없습니다"))
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
                .value(org.hamcrest.Matchers.containsString("원두 그램은 0g 이상이어야 합니다"))
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
