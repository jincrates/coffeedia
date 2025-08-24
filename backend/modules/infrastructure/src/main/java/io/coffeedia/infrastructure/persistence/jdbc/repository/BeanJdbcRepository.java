package io.coffeedia.infrastructure.persistence.jdbc.repository;

import io.coffeedia.infrastructure.persistence.jdbc.entity.BeanJdbcEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BeanJdbcRepository extends ListCrudRepository<BeanJdbcEntity, Long>,
    PagingAndSortingRepository<BeanJdbcEntity, Long> {

}
