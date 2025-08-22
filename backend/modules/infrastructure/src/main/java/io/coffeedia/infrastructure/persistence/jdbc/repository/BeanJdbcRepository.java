package io.coffeedia.infrastructure.persistence.jdbc.repository;

import io.coffeedia.infrastructure.persistence.jdbc.entity.BeanJdbcEntity;
import org.springframework.data.repository.ListCrudRepository;

public interface BeanJdbcRepository extends ListCrudRepository<BeanJdbcEntity, Long> {

}
