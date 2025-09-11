package io.coffeedia.bootstrap.api.controller.dto.auth;

import io.coffeedia.application.usecase.dto.UserResponse;
import lombok.Builder;
import lombok.Getter;

/**
 * 회원가입 응답 DTO
 */
@Getter
@Builder
public class SignUpResponse {

    private final String id;
    private final String username;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String fullName;

    /**
     * UserResponse로부터 SignUpResponse 생성
     */
    public static SignUpResponse from(UserResponse userResponse) {
        return SignUpResponse.builder()
            .id(userResponse.id())
            .username(userResponse.username())
            .email(userResponse.email())
            .firstName(userResponse.firstName())
            .lastName(userResponse.lastName())
            .fullName(userResponse.fullName())
            .build();
    }
}
