package io.coffeedia.infrastructure.persistence.jdbc.repository;

import io.coffeedia.infrastructure.persistence.jdbc.entity.BeanFlavorJdbcEntity;
import org.springframework.data.repository.ListCrudRepository;

public interface BeanFlavorJdbcRepository extends ListCrudRepository<BeanFlavorJdbcEntity, Long> {

}
