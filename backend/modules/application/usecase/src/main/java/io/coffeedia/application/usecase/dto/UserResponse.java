package io.coffeedia.application.usecase.dto;

import io.coffeedia.domain.vo.ActiveStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 응답 DTO
 */
@Getter
@Builder
public class UserResponse {
    
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
}
