package io.coffeedia.infrastructure.persistence.jpa.mapper;

import io.coffeedia.domain.model.User;
import io.coffeedia.infrastructure.persistence.jpa.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

/**
 * User 도메인 모델과 JPA 엔티티 간 변환 매퍼
 */
@Component
public class UserJpaMapper {

    /**
     * JPA 엔티티를 도메인 모델로 변환
     */
    public User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .roles(entity.getRoles())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * 도메인 모델을 JPA 엔티티로 변환
     */
    public UserJpaEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        return UserJpaEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles())
                .status(user.getStatus())
                .build();
    }

    /**
     * 도메인 모델로 기존 JPA 엔티티 업데이트
     */
    public void updateEntity(UserJpaEntity entity, User user) {
        if (entity == null || user == null) {
            return;
        }

        entity.updateInfo(user.getFirstName(), user.getLastName(), user.getEmail());
        entity.changeStatus(user.getStatus());
        
        // 역할 업데이트
        entity.getRoles().clear();
        if (user.getRoles() != null) {
            user.getRoles().forEach(entity::addRole);
        }
    }
}
