package io.coffeedia.infrastructure.persistence.jpa.repository;

import io.coffeedia.infrastructure.persistence.jpa.entity.TagJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagJpaRepository extends JpaRepository<TagJpaEntity, Long> {

    List<TagJpaEntity> findAllByNameIn(List<String> tags);
}
