package io.coffeedia.bootstrap.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import io.coffeedia.IntegrationSupportTest;
import io.coffeedia.application.usecase.dto.UserResponse;
import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import io.coffeedia.bootstrap.api.controller.dto.auth.LoginRequest;
import io.coffeedia.bootstrap.api.controller.dto.auth.LoginResponse;
import io.coffeedia.bootstrap.api.controller.dto.auth.RefreshTokenRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

/**
 * AuthController 통합 테스트
 */
@DisplayName("Auth Controller 통합 테스트")
class AuthControllerTest extends IntegrationSupportTest {

    @BeforeEach
    @Sql(scripts = "/test-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void setUp() {
        // 테스트 데이터가 각 테스트 메서드 실행 전에 로드됩니다
    }

    @Nested
    @DisplayName("로그인 API")
    class LoginApiTest {

        @Test
        @DisplayName("유효한 사용자로 로그인할 수 있다")
        void loginWithValidUser() {
            // given
            LoginRequest request = new LoginRequest("bjorn", "anypassword");

            // when & then
            webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<LoginResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();

                    LoginResponse data = response.data();
                    assertThat(data.accessToken()).isNotNull();
                    assertThat(data.refreshToken()).isNotNull();
                    assertThat(data.tokenType()).isEqualTo("Bearer");
                    assertThat(data.expiresIn()).isEqualTo(3600);
                    assertThat(data.username()).isEqualTo("bjorn");
                    assertThat(data.roles()).contains("customer");
                });
        }

        @Test
        @DisplayName("존재하지 않는 사용자로 로그인하면 404를 반환한다")
        void loginWithNonExistentUser() {
            // given
            LoginRequest request = new LoginRequest("nonexistent", "password");

            // when & then
            webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("사용자를 찾을 수 없습니다");
                });
        }

        @Test
        @DisplayName("잘못된 요청 형식으로 로그인하면 400을 반환한다")
        void loginWithInvalidRequest() {
            // given
            LoginRequest request = new LoginRequest("", "");

            // when & then
            webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                });
        }
    }

    @Nested
    @DisplayName("토큰 갱신 API")
    class RefreshTokenApiTest {

        @Test
        @DisplayName("유효한 리프레시 토큰으로 새로운 토큰을 발급받을 수 있다")
        void refreshWithValidToken() {
            // given
            String refreshToken = jwtTokenProvider.createRefreshToken("bjorn");
            RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);

            // when & then
            webTestClient.post()
                .uri("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<LoginResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();

                    LoginResponse data = response.data();
                    assertThat(data.accessToken()).isNotNull();
                    assertThat(data.refreshToken()).isNotNull();
                    assertThat(data.username()).isEqualTo("bjorn");
                });
        }

        @Test
        @DisplayName("유효하지 않은 리프레시 토큰으로 갱신하면 401을 반환한다")
        void refreshWithInvalidToken() {
            // given
            RefreshTokenRequest request = new RefreshTokenRequest("invalid-refresh-token");

            // when & then
            webTestClient.post()
                .uri("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                });
        }
    }

    @Nested
    @DisplayName("현재 사용자 정보 조회 API")
    class GetCurrentUserApiTest {

        @Test
        @DisplayName("유효한 토큰으로 현재 사용자 정보를 조회할 수 있다")
        void getCurrentUserWithValidToken() {
            // given
            String accessToken = jwtTokenProvider.createAccessToken("bjorn", List.of("customer"));

            // when & then
            webTestClient.get()
                .uri("/api/auth/me")
                .header("Authorization", "Bearer " + accessToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<UserResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();

                    UserResponse data = response.data();
                    assertThat(data.username()).isEqualTo("bjorn");
                    assertThat(data.email()).isEqualTo("bjorn@coffeedia.com");
                    assertThat(data.firstName()).isEqualTo("Bjorn");
                    assertThat(data.lastName()).isEqualTo("Vinterberg");
                    assertThat(data.roles()).contains("customer");
                });
        }

        @Test
        @DisplayName("토큰 없이 현재 사용자 정보를 조회하면 401을 반환한다")
        void getCurrentUserWithoutToken() {
            // when & then
            webTestClient.get()
                .uri("/api/auth/me")
                .exchange()
                .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("유효하지 않은 토큰으로 현재 사용자 정보를 조회하면 401을 반환한다")
        void getCurrentUserWithInvalidToken() {
            // when & then
            webTestClient.get()
                .uri("/api/auth/me")
                .header("Authorization", "Bearer invalid-token")
                .exchange()
                .expectStatus().isUnauthorized();
        }
    }

    @Nested
    @DisplayName("로그아웃 API")
    class LogoutApiTest {

        @Test
        @DisplayName("유효한 토큰으로 로그아웃할 수 있다")
        void logoutWithValidToken() {
            // given
            String accessToken = jwtTokenProvider.createAccessToken("bjorn", List.of("customer"));

            // when & then
            webTestClient.post()
                .uri("/api/auth/logout")
                .header("Authorization", "Bearer " + accessToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                });
        }

        @Test
        @DisplayName("토큰 없이 로그아웃하면 401을 반환한다")
        void logoutWithoutToken() {
            // when & then
            webTestClient.post()
                .uri("/api/auth/logout")
                .exchange()
                .expectStatus().isUnauthorized();
        }
    }

    @Nested
    @DisplayName("토큰 검증 API")
    class ValidateTokenApiTest {

        @Test
        @DisplayName("유효한 토큰을 검증할 수 있다")
        void validateValidToken() {
            // given
            String accessToken = jwtTokenProvider.createAccessToken("bjorn", List.of("customer"));

            // when & then
            webTestClient.post()
                .uri("/api/auth/validate")
                .header("Authorization", "Bearer " + accessToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Boolean>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isTrue();
                });
        }

        @Test
        @DisplayName("유효하지 않은 토큰 검증은 false를 반환한다")
        void validateInvalidToken() {
            // when & then
            webTestClient.post()
                .uri("/api/auth/validate")
                .header("Authorization", "Bearer invalid-token")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Boolean>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isFalse();
                });
        }
    }
}
