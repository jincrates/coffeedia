package io.coffeedia.bootstrap.api.controller;

import io.coffeedia.IntegrationSupportTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@Tag("integration")
class HealthControllerTest extends IntegrationSupportTest {

    @Nested
    @DisplayName("헬스 체크")
    class HealthCheckTest {

        @Test
        @DisplayName("GET /health 요청 시 서버 상태 정보가 반환된다")
        void getHealthCheckReturnsServerStatus() {
            // when & then
            webTestClient.get()
                .uri("/health")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .expectBody(String.class)
                .value(response -> {
                    assert response != null : "응답이 null입니다";
                    assert response.contains(
                        "It's Working in Coffeedia Backend") : "기본 메시지가 포함되어야 합니다";
                    assert response.contains("LOCAL PORT") : "로컬 포트 정보가 포함되어야 합니다";
                    assert response.contains("SERVER PORT") : "서버 포트 정보가 포함되어야 합니다";
                });
        }

        @Test
        @DisplayName("헬스 체크 응답에 포트 정보가 포함된다")
        void healthCheckResponseIncludesPortInformation() {
            // when & then
            webTestClient.get()
                .uri("/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> {
                    // 포트 번호 패턴 검증 (숫자가 포함된 포트 정보)
                    assert response.matches(".*LOCAL PORT \\d+.*") : "로컬 포트 번호가 올바른 형식이어야 합니다";
                    assert response.matches(".*SERVER PORT \\d+.*") : "서버 포트 번호가 올바른 형식이어야 합니다";
                });
        }

        @Test
        @DisplayName("다양한 Accept 헤더로 요청해도 텍스트 응답을 반환한다")
        void healthCheckWorksWithDifferentAcceptHeaders() {
            // JSON Accept 헤더로 요청
            webTestClient.get()
                .uri("/health")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> {
                    assert response != null : "JSON Accept 헤더 요청에도 응답해야 합니다";
                    assert response.contains(
                        "It's Working in Coffeedia Backend") : "기본 메시지가 포함되어야 합니다";
                });

            // 모든 타입 Accept 헤더로 요청
            webTestClient.get()
                .uri("/health")
                .accept(MediaType.ALL)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> {
                    assert response != null : "모든 타입 Accept 헤더 요청에도 응답해야 합니다";
                    assert response.contains(
                        "It's Working in Coffeedia Backend") : "기본 메시지가 포함되어야 합니다";
                });
        }

        @Test
        @DisplayName("여러 번 요청해도 일관된 응답을 반환한다")
        void healthCheckReturnsConsistentResponse() {
            String firstResponse = webTestClient.get()
                .uri("/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

            String secondResponse = webTestClient.get()
                .uri("/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

            // 응답 형식은 동일해야 함 (포트는 같을 것으로 예상)
            assert firstResponse != null && secondResponse != null : "모든 응답이 null이 아니어야 합니다";
            assert firstResponse.equals(secondResponse) : "여러 번 요청해도 같은 응답을 반환해야 합니다";
        }

        @Test
        @DisplayName("HEAD 메서드로 요청시에도 정상 응답한다")
        void healthCheckWorksWithHeadMethod() {
            // when & then
            webTestClient.method(org.springframework.http.HttpMethod.HEAD)
                .uri("/health")
                .exchange()
                .expectStatus().isOk();
        }
    }
}
