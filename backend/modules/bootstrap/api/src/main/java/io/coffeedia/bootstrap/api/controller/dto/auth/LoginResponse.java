package io.coffeedia.bootstrap.api.controller.dto.auth;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 로그인 응답 DTO
 */
@Getter
@Builder
public class LoginResponse {
    
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;
    private final Long expiresIn;
    private final String username;
    private final List<String> roles;
}
