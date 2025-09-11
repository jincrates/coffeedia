package io.coffeedia.application.usecase.service;

import io.coffeedia.application.port.repository.UserRepositoryPort;
import io.coffeedia.application.usecase.CreateUserUseCase;
import io.coffeedia.application.usecase.dto.CreateUserCommand;
import io.coffeedia.application.usecase.dto.UserResponse;
import io.coffeedia.application.usecase.mapper.UserMapper;
import io.coffeedia.domain.exception.UserAlreadyExistsException;
import io.coffeedia.domain.model.User;
import io.coffeedia.domain.vo.ActiveStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원가입 UseCase 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
class CreateUserService implements CreateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse execute(CreateUserCommand command) {
        log.info("회원가입 처리 시작: {}", command.username());

        // 중복 확인
        validateUserNotExists(command);

        // 사용자 생성
        User user = createUser(command);

        // 저장
        User savedUser = userRepositoryPort.save(user);

        log.info("회원가입 완료: {} (ID: {})", savedUser.getUsername(), savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    private void validateUserNotExists(CreateUserCommand command) {
        if (userRepositoryPort.existsByUsername(command.username())) {
            throw new UserAlreadyExistsException("이미 존재하는 사용자명입니다: " + command.username());
        }

        if (userRepositoryPort.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException("이미 존재하는 이메일입니다: " + command.email());
        }
    }

    private User createUser(CreateUserCommand command) {
        LocalDateTime now = LocalDateTime.now();

        return User.builder()
            .id(UUID.randomUUID().toString())
            .username(command.username())
            .email(command.email())
            .password(command.password()) // 이미 암호화된 비밀번호 사용
            .firstName(command.firstName())
            .lastName(command.lastName())
            .roles(List.of("customer")) // 기본 역할은 customer
            .status(ActiveStatus.ACTIVE)
            .createdAt(now)
            .updatedAt(now)
            .build();
    }
}
