package io.coffeedia.application.usecase.mapper;

import io.coffeedia.application.usecase.dto.UserResponse;
import io.coffeedia.domain.model.User;
import org.springframework.stereotype.Component;

/**
 * 사용자 도메인 모델과 DTO 간 변환을 담당하는 매퍼
 */
@Component
public class UserMapper {

    /**
     * User 도메인 모델을 UserResponse DTO로 변환
     */
    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
