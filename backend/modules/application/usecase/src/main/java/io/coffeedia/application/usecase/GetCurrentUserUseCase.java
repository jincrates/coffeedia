package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.UserResponse;

/**
 * 현재 사용자 정보 조회 UseCase
 */
public interface GetCurrentUserUseCase {

    /**
     * 사용자명으로 현재 사용자 정보 조회
     */
    UserResponse execute(String username);

    /**
     * 사용자 ID로 현재 사용자 정보 조회
     */
    UserResponse executeById(String userId);
}
