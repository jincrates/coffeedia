package io.coffeedia.application.usecase;

import io.coffeedia.application.port.repository.UserRepositoryPort;
import io.coffeedia.application.usecase.dto.UserResponse;
import io.coffeedia.application.usecase.mapper.UserMapper;
import io.coffeedia.domain.exception.UserNotFoundException;
import io.coffeedia.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 현재 사용자 정보 조회 UseCase
 */
@Service
@RequiredArgsConstructor
public class GetCurrentUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;

    /**
     * 사용자명으로 현재 사용자 정보 조회
     */
    public UserResponse execute(String username) {
        User user = userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + username));

        return userMapper.toResponse(user);
    }

    /**
     * 사용자 ID로 현재 사용자 정보 조회
     */
    public UserResponse executeById(String userId) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        return userMapper.toResponse(user);
    }
}
