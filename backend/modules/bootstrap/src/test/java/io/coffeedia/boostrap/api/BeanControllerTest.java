package io.coffeedia.boostrap.api;

import io.coffeedia.boostrap.IntegrationSupportTest;
import java.time.LocalDate;
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
}
