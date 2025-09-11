package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.CreateUserCommand;
import io.coffeedia.application.usecase.dto.UserResponse;

/**
 * 회원가입 UseCase
 */
public interface CreateUserUseCase {

    /**
     * 회원가입 처리
     */
    UserResponse execute(CreateUserCommand command);
}
