package io.coffeedia.bootstrap.api.controller.dto.auth;

import io.coffeedia.application.usecase.dto.UserResponse;
import io.coffeedia.domain.vo.ActiveStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 정보 응답 DTO
 */
@Getter
@Builder
public class UserInfoResponse {
    
    private final String id;
    private final String username;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String fullName;
    private final List<String> roles;
    private final ActiveStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    /**
     * UserResponse로부터 UserInfoResponse 생성
     */
    public static UserInfoResponse from(UserResponse userResponse) {
        return UserInfoResponse.builder()
                .id(userResponse.getId())
                .username(userResponse.getUsername())
                .email(userResponse.getEmail())
                .firstName(userResponse.getFirstName())
                .lastName(userResponse.getLastName())
                .fullName(userResponse.getFullName())
                .roles(userResponse.getRoles())
                .status(userResponse.getStatus())
                .createdAt(userResponse.getCreatedAt())
                .updatedAt(userResponse.getUpdatedAt())
                .build();
    }
}
