package io.coffeedia.infrastructure.persistence.jpa.repository;

import io.coffeedia.infrastructure.persistence.jpa.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 JPA Repository
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {
    
    /**
     * 사용자명으로 조회
     */
    Optional<UserJpaEntity> findByUsername(String username);

    /**
     * 이메일로 조회
     */
    Optional<UserJpaEntity> findByEmail(String email);

    /**
     * 사용자명 존재 여부 확인
     */
    boolean existsByUsername(String username);

    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);
}
