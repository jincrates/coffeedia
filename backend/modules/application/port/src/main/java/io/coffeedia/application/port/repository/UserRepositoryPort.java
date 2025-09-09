package io.coffeedia.application.port.repository;

import io.coffeedia.domain.model.User;

import java.util.Optional;

/**
 * 사용자 저장소 포트
 */
public interface UserRepositoryPort {
    
    /**
     * 사용자명으로 사용자 조회
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);

    /**
     * 사용자 ID로 사용자 조회
     */
    Optional<User> findById(String id);

    /**
     * 사용자 저장
     */
    User save(User user);

    /**
     * 사용자 존재 여부 확인
     */
    boolean existsByUsername(String username);

    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);
}
