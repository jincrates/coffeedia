package io.coffeedia.infrastructure.persistence.jpa.repository;

import io.coffeedia.infrastructure.persistence.jpa.entity.FlavorJpaEntity;
import org.springframework.data.repository.ListCrudRepository;

public interface FlavorJpaRepository extends ListCrudRepository<FlavorJpaEntity, Long> {

}
