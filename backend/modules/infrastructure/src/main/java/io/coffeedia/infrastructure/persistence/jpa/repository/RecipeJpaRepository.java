package io.coffeedia.infrastructure.persistence.jpa.repository;

import io.coffeedia.infrastructure.persistence.jpa.entity.RecipeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeJpaRepository extends JpaRepository<RecipeJpaEntity, Long> {

}
