package io.coffeedia.bootstrap.api.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import io.coffeedia.application.port.repository.UserRepositoryPort;
import io.coffeedia.bootstrap.api.controller.dto.auth.LoginRequest;
import io.coffeedia.bootstrap.api.controller.dto.auth.LoginResponse;
import io.coffeedia.domain.exception.UnauthorizedException;
import io.coffeedia.domain.exception.UserNotFoundException;
import io.coffeedia.domain.model.User;
import io.coffeedia.domain.vo.ActiveStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * AuthenticationService 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("인증 서비스")
class AuthenticationServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        sampleUser = User.builder()
            .id("user-id")
            .username("testuser")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .roles(List.of("customer"))
            .status(ActiveStatus.ACTIVE)
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    @Nested
    @DisplayName("로그인")
    class LoginTest {

        @Test
        @DisplayName("존재하는 활성 사용자로 로그인할 수 있다")
        void loginWithValidActiveUser() {
            // given
            LoginRequest request = new LoginRequest("testuser", "password");
            String accessToken = "access-token";
            String refreshToken = "refresh-token";

            given(userRepositoryPort.findByUsername("testuser")).willReturn(
                Optional.of(sampleUser));
            given(jwtTokenProvider.createAccessToken("testuser", List.of("customer"))).willReturn(
                accessToken);
            given(jwtTokenProvider.createRefreshToken("testuser")).willReturn(refreshToken);

            // when
            LoginResponse response = authenticationService.login(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo(accessToken);
            assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
            assertThat(response.getTokenType()).isEqualTo("Bearer");
            assertThat(response.getExpiresIn()).isEqualTo(3600L);
            assertThat(response.getUsername()).isEqualTo("testuser");
            assertThat(response.getRoles()).containsExactly("customer");

            verify(userRepositoryPort).findByUsername("testuser");
            verify(jwtTokenProvider).createAccessToken("testuser", List.of("customer"));
            verify(jwtTokenProvider).createRefreshToken("testuser");
        }

        @Test
        @DisplayName("존재하지 않는 사용자로 로그인하면 예외를 발생시킨다")
        void loginWithNonExistentUser() {
            // given
            LoginRequest request = new LoginRequest("nonexistent", "password");
            given(userRepositoryPort.findByUsername("nonexistent")).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authenticationService.login(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다: nonexistent");

            verify(userRepositoryPort).findByUsername("nonexistent");
        }

        @Test
        @DisplayName("비활성 사용자로 로그인하면 예외를 발생시킨다")
        void loginWithInactiveUser() {
            // given
            User inactiveUser = User.builder()
                .id("user-id")
                .username("inactiveuser")
                .email("inactive@example.com")
                .status(ActiveStatus.INACTIVE)
                .build();

            LoginRequest request = new LoginRequest("inactiveuser", "password");
            given(userRepositoryPort.findByUsername("inactiveuser")).willReturn(
                Optional.of(inactiveUser));

            // when & then
            assertThatThrownBy(() -> authenticationService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("비활성화된 사용자입니다");

            verify(userRepositoryPort).findByUsername("inactiveuser");
        }
    }

    @Nested
    @DisplayName("토큰 갱신")
    class RefreshTokenTest {

        @Test
        @DisplayName("유효한 리프레시 토큰으로 새로운 토큰을 발급받을 수 있다")
        void refreshTokenWithValidToken() {
            // given
            String refreshToken = "valid-refresh-token";
            String newAccessToken = "new-access-token";
            String newRefreshToken = "new-refresh-token";

            given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
            given(jwtTokenProvider.isRefreshToken(refreshToken)).willReturn(true);
            given(jwtTokenProvider.getUsernameFromToken(refreshToken)).willReturn("testuser");
            given(userRepositoryPort.findByUsername("testuser")).willReturn(
                Optional.of(sampleUser));
            given(jwtTokenProvider.createAccessToken("testuser", List.of("customer"))).willReturn(
                newAccessToken);
            given(jwtTokenProvider.createRefreshToken("testuser")).willReturn(newRefreshToken);

            // when
            LoginResponse response = authenticationService.refreshToken(refreshToken);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo(newAccessToken);
            assertThat(response.getRefreshToken()).isEqualTo(newRefreshToken);
            assertThat(response.getUsername()).isEqualTo("testuser");

            verify(jwtTokenProvider).validateToken(refreshToken);
            verify(jwtTokenProvider).isRefreshToken(refreshToken);
            verify(jwtTokenProvider).getUsernameFromToken(refreshToken);
            verify(userRepositoryPort).findByUsername("testuser");
            verify(jwtTokenProvider).createAccessToken("testuser", List.of("customer"));
            verify(jwtTokenProvider).createRefreshToken("testuser");
        }

        @Test
        @DisplayName("유효하지 않은 리프레시 토큰으로 갱신하면 예외를 발생시킨다")
        void refreshTokenWithInvalidToken() {
            // given
            String invalidRefreshToken = "invalid-refresh-token";
            given(jwtTokenProvider.validateToken(invalidRefreshToken)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authenticationService.refreshToken(invalidRefreshToken))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("유효하지 않은 리프레시 토큰입니다");

            verify(jwtTokenProvider).validateToken(invalidRefreshToken);
        }

        @Test
        @DisplayName("Access Token으로 갱신하면 예외를 발생시킨다")
        void refreshTokenWithAccessToken() {
            // given
            String accessToken = "access-token";
            given(jwtTokenProvider.validateToken(accessToken)).willReturn(true);
            given(jwtTokenProvider.isRefreshToken(accessToken)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authenticationService.refreshToken(accessToken))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("유효하지 않은 리프레시 토큰입니다");

            verify(jwtTokenProvider).validateToken(accessToken);
            verify(jwtTokenProvider).isRefreshToken(accessToken);
        }
    }

    @Nested
    @DisplayName("토큰 검증")
    class ValidateTokenTest {

        @Test
        @DisplayName("유효한 Bearer 토큰을 검증할 수 있다")
        void validateValidBearerToken() {
            // given
            String authorizationHeader = "Bearer valid-token";
            given(jwtTokenProvider.validateToken("valid-token")).willReturn(true);

            // when
            boolean result = authenticationService.validateToken(authorizationHeader);

            // then
            assertThat(result).isTrue();
            verify(jwtTokenProvider).validateToken("valid-token");
        }

        @Test
        @DisplayName("유효하지 않은 토큰은 검증에 실패한다")
        void validateInvalidToken() {
            // given
            String authorizationHeader = "Bearer invalid-token";
            given(jwtTokenProvider.validateToken("invalid-token")).willReturn(false);

            // when
            boolean result = authenticationService.validateToken(authorizationHeader);

            // then
            assertThat(result).isFalse();
            verify(jwtTokenProvider).validateToken("invalid-token");
        }

        @Test
        @DisplayName("Bearer 형식이 아닌 헤더는 검증에 실패한다")
        void validateNonBearerHeader() {
            // given
            String authorizationHeader = "Basic token";

            // when
            boolean result = authenticationService.validateToken(authorizationHeader);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("null 헤더는 검증에 실패한다")
        void validateNullHeader() {
            // when
            boolean result = authenticationService.validateToken(null);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("로그아웃")
    class LogoutTest {

        @Test
        @DisplayName("로그아웃을 처리할 수 있다")
        void logout() {
            // given
            String username = "testuser";

            // when & then (예외 없이 실행되어야 함)
            authenticationService.logout(username);

            // 현재는 단순 로깅만 하므로 별도 검증 없음
            // 추후 블랙리스트 기능 구현시 검증 로직 추가
        }
    }
}
