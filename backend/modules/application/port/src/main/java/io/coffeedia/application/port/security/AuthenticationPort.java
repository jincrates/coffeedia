package io.coffeedia.application.port.security;

import io.coffeedia.domain.model.User;

import java.util.Optional;

/**
 * 인증 관련 포트
 */
public interface AuthenticationPort {
    
    /**
     * 사용자 인증
     */
    Optional<User> authenticate(String username, String password);

    /**
     * 토큰으로 사용자 정보 조회
     */
    Optional<User> getUserFromToken(String token);

    /**
     * 토큰 유효성 검증
     */
    boolean validateToken(String token);

    /**
     * 로그아웃 처리
     */
    void logout(String username);
}
