package io.coffeedia.bootstrap.api.controller.dto.auth;

import io.coffeedia.application.usecase.dto.UserResponse;
import io.coffeedia.domain.vo.ActiveStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

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
            .id(userResponse.id())
            .username(userResponse.username())
            .email(userResponse.email())
            .firstName(userResponse.firstName())
            .lastName(userResponse.lastName())
            .fullName(userResponse.fullName())
            .roles(userResponse.roles())
            .status(userResponse.status())
            .createdAt(userResponse.createdAt())
            .updatedAt(userResponse.updatedAt())
            .build();
    }
}
