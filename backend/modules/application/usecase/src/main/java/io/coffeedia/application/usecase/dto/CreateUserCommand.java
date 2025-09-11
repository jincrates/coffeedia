package io.coffeedia.application.usecase.dto;

import lombok.Builder;

/**
 * 회원가입 명령 DTO
 */
@Builder
public record CreateUserCommand(
    String username,
    String email,
    String password,
    String firstName,
    String lastName
) {

}
