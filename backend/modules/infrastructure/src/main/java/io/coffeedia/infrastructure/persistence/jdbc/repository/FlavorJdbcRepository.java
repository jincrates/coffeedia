package io.coffeedia.infrastructure.persistence.jdbc.repository;

import io.coffeedia.infrastructure.persistence.jdbc.entity.FlavorJdbcEntity;
import org.springframework.data.repository.ListCrudRepository;

public interface FlavorJdbcRepository extends ListCrudRepository<FlavorJdbcEntity, Long> {

}
