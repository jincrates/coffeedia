package io.coffeedia.bootstrap.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.coffeedia.IntegrationSupportTest;
import io.coffeedia.bootstrap.api.controller.dto.auth.LoginRequest;
import io.coffeedia.bootstrap.api.controller.dto.auth.RefreshTokenRequest;
import io.coffeedia.bootstrap.api.security.JwtTokenProvider;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

/**
 * AuthController 통합 테스트
 */
@DisplayName("Auth Controller 통합 테스트")
@Sql("/test-data.sql")
class AuthControllerTest extends IntegrationSupportTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Nested
    @DisplayName("로그인 API")
    class LoginApiTest {

        @Test
        @DisplayName("유효한 사용자로 로그인할 수 있다")
        void loginWithValidUser() throws Exception {
            // given
            LoginRequest request = new LoginRequest("bjorn", "anypassword");

            // when & then
            mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(3600))
                .andExpect(jsonPath("$.data.username").value("bjorn"))
                .andExpect(jsonPath("$.data.roles[0]").value("customer"));
        }

        @Test
        @DisplayName("존재하지 않는 사용자로 로그인하면 404를 반환한다")
        void loginWithNonExistentUser() throws Exception {
            // given
            LoginRequest request = new LoginRequest("nonexistent", "password");

            // when & then
            mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다: nonexistent"));
        }

        @Test
        @DisplayName("잘못된 요청 형식으로 로그인하면 400을 반환한다")
        void loginWithInvalidRequest() throws Exception {
            // given
            String invalidJson = "{\"username\":\"\",\"password\":\"\"}";

            // when & then
            mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("토큰 갱신 API")
    class RefreshTokenApiTest {

        @Test
        @DisplayName("유효한 리프레시 토큰으로 새로운 토큰을 발급받을 수 있다")
        void refreshWithValidToken() throws Exception {
            // given
            String refreshToken = jwtTokenProvider.createRefreshToken("bjorn");
            RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);

            // when & then
            mockMvc.perform(post("/api/auth/refresh")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.username").value("bjorn"));
        }

        @Test
        @DisplayName("유효하지 않은 리프레시 토큰으로 갱신하면 401을 반환한다")
        void refreshWithInvalidToken() throws Exception {
            // given
            RefreshTokenRequest request = new RefreshTokenRequest("invalid-refresh-token");

            // when & then
            mockMvc.perform(post("/api/auth/refresh")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("현재 사용자 정보 조회 API")
    class GetCurrentUserApiTest {

        @Test
        @DisplayName("유효한 토큰으로 현재 사용자 정보를 조회할 수 있다")
        void getCurrentUserWithValidToken() throws Exception {
            // given
            String accessToken = jwtTokenProvider.createAccessToken("bjorn", List.of("customer"));

            // when & then
            mockMvc.perform(get("/api/auth/me")
                    .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("bjorn"))
                .andExpect(jsonPath("$.data.email").value("bjorn@coffeedia.com"))
                .andExpect(jsonPath("$.data.firstName").value("Bjorn"))
                .andExpect(jsonPath("$.data.lastName").value("Vinterberg"))
                .andExpect(jsonPath("$.data.roles[0]").value("customer"));
        }

        @Test
        @DisplayName("토큰 없이 현재 사용자 정보를 조회하면 401을 반환한다")
        void getCurrentUserWithoutToken() throws Exception {
            // when & then
            mockMvc.perform(get("/api/auth/me"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("유효하지 않은 토큰으로 현재 사용자 정보를 조회하면 401을 반환한다")
        void getCurrentUserWithInvalidToken() throws Exception {
            // when & then
            mockMvc.perform(get("/api/auth/me")
                    .header("Authorization", "Bearer invalid-token"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("로그아웃 API")
    class LogoutApiTest {

        @Test
        @DisplayName("유효한 토큰으로 로그아웃할 수 있다")
        void logoutWithValidToken() throws Exception {
            // given
            String accessToken = jwtTokenProvider.createAccessToken("bjorn", List.of("customer"));

            // when & then
            mockMvc.perform(post("/api/auth/logout")
                    .with(csrf())
                    .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("토큰 없이 로그아웃하면 401을 반환한다")
        void logoutWithoutToken() throws Exception {
            // when & then
            mockMvc.perform(post("/api/auth/logout")
                    .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("토큰 검증 API")
    class ValidateTokenApiTest {

        @Test
        @DisplayName("유효한 토큰을 검증할 수 있다")
        void validateValidToken() throws Exception {
            // given
            String accessToken = jwtTokenProvider.createAccessToken("bjorn", List.of("customer"));

            // when & then
            mockMvc.perform(post("/api/auth/validate")
                    .with(csrf())
                    .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
        }

        @Test
        @DisplayName("유효하지 않은 토큰 검증은 false를 반환한다")
        void validateInvalidToken() throws Exception {
            // when & then
            mockMvc.perform(post("/api/auth/validate")
                    .with(csrf())
                    .header("Authorization", "Bearer invalid-token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(false));
        }
    }
}
