package io.coffeedia.bootstrap.api.security;

import io.coffeedia.application.port.repository.UserRepositoryPort;
import io.coffeedia.bootstrap.api.controller.dto.auth.LoginRequest;
import io.coffeedia.bootstrap.api.controller.dto.auth.LoginResponse;
import io.coffeedia.domain.exception.UnauthorizedException;
import io.coffeedia.domain.exception.UserNotFoundException;
import io.coffeedia.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 인증 관련 비즈니스 로직을 처리하는 서비스
 * <p>
 * 현재는 임시로 자체 JWT 구현을 사용합니다. 추후 Keycloak 또는 다른 인증 서버와 연동 가능합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepositoryPort userRepositoryPort;
    private final JwtTokenProvider jwtTokenProvider;
    // TODO: Keycloak 연동시 KeycloakAuthenticationAdapter 추가

    /**
     * 로그인 처리
     * <p>
     * 현재는 데이터베이스에 저장된 사용자 정보로만 검증합니다. 실제 비밀번호 검증은 추후 구현될 예정입니다.
     */
    public LoginResponse login(LoginRequest request) {
        // TODO: 실제로는 비밀번호 검증 로직 추가 필요
        // 현재는 임시로 사용자 존재 여부만 확인
        User user = userRepositoryPort.findByUsername(request.getUsername())
            .orElseThrow(
                () -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + request.getUsername()));

        if (!user.isActive()) {
            throw new UnauthorizedException("비활성화된 사용자입니다");
        }

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(
            user.getUsername(),
            user.getRoles()
        );
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUsername());

        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(3600L) // 1시간
            .username(user.getUsername())
            .roles(user.getRoles())
            .build();
    }

    /**
     * 토큰 갱신
     */
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)
            || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new UnauthorizedException("유효하지 않은 리프레시 토큰입니다");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepositoryPort.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + username));

        if (!user.isActive()) {
            throw new UnauthorizedException("비활성화된 사용자입니다");
        }

        // 새로운 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(
            user.getUsername(),
            user.getRoles()
        );
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getUsername());

        return LoginResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .tokenType("Bearer")
            .expiresIn(3600L)
            .username(user.getUsername())
            .roles(user.getRoles())
            .build();
    }

    /**
     * 로그아웃 처리
     */
    public void logout(String username) {
        // TODO: 블랙리스트에 토큰 추가 또는 로그아웃 처리 로직 추가
        log.info("사용자 로그아웃: {}", username);
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authorizationHeader.substring("Bearer ".length());
        return jwtTokenProvider.validateToken(token);
    }
}
