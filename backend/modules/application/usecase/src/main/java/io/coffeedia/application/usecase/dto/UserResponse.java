package io.coffeedia.application.usecase.dto;

import io.coffeedia.domain.vo.ActiveStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

/**
 * 사용자 응답 DTO
 */
@Builder
public record UserResponse(
    String id,
    String username,
    String email,
    String firstName,
    String lastName,
    String fullName,
    List<String> roles,
    ActiveStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}
