package io.coffeedia.infrastructure.persistence.jpa.repository;

import io.coffeedia.infrastructure.persistence.jpa.entity.BeanFlavorJpaEntity;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;

public interface BeanFlavorJpaRepository extends ListCrudRepository<BeanFlavorJpaEntity, Long> {

    List<BeanFlavorJpaEntity> findAllByBeanId(Long beanId);

    List<BeanFlavorJpaEntity> findAllByBeanIdIn(List<Long> beanIds);
}
