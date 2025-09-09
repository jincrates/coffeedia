package io.coffeedia.infrastructure.persistence.jpa;

import io.coffeedia.application.port.repository.UserRepositoryPort;
import io.coffeedia.domain.model.User;
import io.coffeedia.infrastructure.persistence.jpa.entity.UserJpaEntity;
import io.coffeedia.infrastructure.persistence.jpa.mapper.UserJpaMapper;
import io.coffeedia.infrastructure.persistence.jpa.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 저장소 어댑터
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserJpaMapper userJpaMapper;

    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("사용자명으로 사용자 조회: {}", username);
        
        return userJpaRepository.findByUsername(username)
                .map(userJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.debug("이메일로 사용자 조회: {}", email);
        
        return userJpaRepository.findByEmail(email)
                .map(userJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findById(String id) {
        log.debug("ID로 사용자 조회: {}", id);
        
        return userJpaRepository.findById(id)
                .map(userJpaMapper::toDomain);
    }

    @Override
    public User save(User user) {
        log.debug("사용자 저장: {}", user.getUsername());
        
        UserJpaEntity entity;
        
        if (user.getId() != null) {
            // 기존 사용자 업데이트
            entity = userJpaRepository.findById(user.getId())
                    .orElse(userJpaMapper.toEntity(user));
            userJpaMapper.updateEntity(entity, user);
        } else {
            // 새로운 사용자 생성
            entity = userJpaMapper.toEntity(user);
        }
        
        UserJpaEntity savedEntity = userJpaRepository.save(entity);
        return userJpaMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByUsername(String username) {
        log.debug("사용자명 존재 여부 확인: {}", username);
        
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("이메일 존재 여부 확인: {}", email);
        
        return userJpaRepository.existsByEmail(email);
    }
}
