package io.coffeedia.bootstrap.api.security;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.LocalDateTime;
import java.util.List;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * JwtTokenProvider 테스트
 */
@DisplayName("JWT 토큰 제공자")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        String secret = "test-jwt-secret-key-for-testing-purposes-must-be-long-enough";
        jwtTokenProvider = new JwtTokenProvider(secret, 3600000L, 86400000L);
        secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Nested
    @DisplayName("Access Token 생성")
    class CreateAccessTokenTest {

        @Test
        @DisplayName("사용자명과 역할로 Access Token을 생성할 수 있다")
        void createAccessToken() {
            // given
            String username = "testuser";
            List<String> roles = List.of("customer", "employee");

            // when
            String token = jwtTokenProvider.createAccessToken(username, roles);

            // then
            assertThat(token).isNotBlank();

            // 토큰 검증
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

            assertThat(claims.getSubject()).isEqualTo(username);
            assertThat(claims.get("tokenType")).isEqualTo("access");
            assertThat((List<String>) claims.get("roles")).containsExactly("customer", "employee");
        }

        @Test
        @DisplayName("역할이 없어도 Access Token을 생성할 수 있다")
        void createAccessTokenWithoutRoles() {
            // given
            String username = "testuser";

            // when
            String token = jwtTokenProvider.createAccessToken(username, List.of());

            // then
            assertThat(token).isNotBlank();

            Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

            assertThat(claims.getSubject()).isEqualTo(username);
            assertThat(claims.get("tokenType")).isEqualTo("access");
            assertThat((List<String>) claims.get("roles")).isEmpty();
        }
    }

    @Nested
    @DisplayName("Refresh Token 생성")
    class CreateRefreshTokenTest {

        @Test
        @DisplayName("사용자명으로 Refresh Token을 생성할 수 있다")
        void createRefreshToken() {
            // given
            String username = "testuser";

            // when
            String token = jwtTokenProvider.createRefreshToken(username);

            // then
            assertThat(token).isNotBlank();

            Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

            assertThat(claims.getSubject()).isEqualTo(username);
            assertThat(claims.get("tokenType")).isEqualTo("refresh");
            assertThat(claims.get("roles")).isNull(); // Refresh Token에는 역할 정보 없음
        }
    }

    @Nested
    @DisplayName("토큰에서 정보 추출")
    class ExtractFromTokenTest {

        @Test
        @DisplayName("토큰에서 사용자명을 추출할 수 있다")
        void getUsernameFromToken() {
            // given
            String username = "testuser";
            String token = jwtTokenProvider.createAccessToken(username, List.of("customer"));

            // when
            String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);

            // then
            assertThat(extractedUsername).isEqualTo(username);
        }

        @Test
        @DisplayName("토큰에서 역할 정보를 추출할 수 있다")
        void getRolesFromToken() {
            // given
            String username = "testuser";
            List<String> roles = List.of("customer", "employee");
            String token = jwtTokenProvider.createAccessToken(username, roles);

            // when
            List<String> extractedRoles = jwtTokenProvider.getRolesFromToken(token);

            // then
            assertThat(extractedRoles).containsExactly("customer", "employee");
        }

        @Test
        @DisplayName("토큰에서 만료 시간을 추출할 수 있다")
        void getExpirationFromToken() {
            // given
            String username = "testuser";
            String token = jwtTokenProvider.createAccessToken(username, List.of("customer"));

            // when
            LocalDateTime expiration = jwtTokenProvider.getExpirationFromToken(token);

            // then
            assertThat(expiration).isAfter(LocalDateTime.now());
            assertThat(expiration).isBefore(LocalDateTime.now().plusHours(2)); // 1시간 + 여유시간
        }
    }

    @Nested
    @DisplayName("토큰 유효성 검증")
    class ValidateTokenTest {

        @Test
        @DisplayName("유효한 토큰은 검증을 통과한다")
        void validateValidToken() {
            // given
            String username = "testuser";
            String token = jwtTokenProvider.createAccessToken(username, List.of("customer"));

            // when
            boolean isValid = jwtTokenProvider.validateToken(token);

            // then
            assertThat(isValid).isTrue();
        }
    }

    @Nested
    @DisplayName("토큰 타입 확인")
    class TokenTypeTest {

        @Test
        @DisplayName("Access Token 타입을 확인할 수 있다")
        void isAccessToken() {
            // given
            String accessToken = jwtTokenProvider.createAccessToken("testuser",
                List.of("customer"));
            String refreshToken = jwtTokenProvider.createRefreshToken("testuser");

            // when & then
            assertThat(jwtTokenProvider.isAccessToken(accessToken)).isTrue();
            assertThat(jwtTokenProvider.isAccessToken(refreshToken)).isFalse();
        }

        @Test
        @DisplayName("Refresh Token 타입을 확인할 수 있다")
        void isRefreshToken() {
            // given
            String accessToken = jwtTokenProvider.createAccessToken("testuser",
                List.of("customer"));
            String refreshToken = jwtTokenProvider.createRefreshToken("testuser");

            // when & then
            assertThat(jwtTokenProvider.isRefreshToken(refreshToken)).isTrue();
            assertThat(jwtTokenProvider.isRefreshToken(accessToken)).isFalse();
        }
    }
}
