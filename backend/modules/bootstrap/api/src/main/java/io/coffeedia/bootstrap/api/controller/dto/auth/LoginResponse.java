package io.coffeedia.bootstrap.api.controller.dto.auth;

import java.util.List;
import lombok.Builder;

/**
 * 로그인 응답 DTO
 */
@Builder
public record LoginResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long expiresIn,
    String username,
    List<String> roles
) {

}
