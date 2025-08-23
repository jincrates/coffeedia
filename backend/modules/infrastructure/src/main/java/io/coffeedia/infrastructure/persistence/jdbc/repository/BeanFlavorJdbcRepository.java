package io.coffeedia.infrastructure.persistence.jdbc.repository;

import io.coffeedia.infrastructure.persistence.jdbc.entity.BeanFlavorJdbcEntity;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;

public interface BeanFlavorJdbcRepository extends ListCrudRepository<BeanFlavorJdbcEntity, Long> {

    List<BeanFlavorJdbcEntity> findAllByBeanId(Long beanId);
}
