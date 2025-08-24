package io.coffeedia.infrastructure.persistence.jpa.repository;

import io.coffeedia.infrastructure.persistence.jpa.entity.BeanJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BeanJpaRepository extends JpaRepository<BeanJpaEntity, Long> {

    @Query("""
        SELECT b FROM BeanJpaEntity b
        LEFT JOIN FETCH b.beanFlavors bf
        LEFT JOIN FETCH bf.flavor f
        WHERE b IN :beans
        """)
    List<BeanJpaEntity> findAllWithFlavors(@Param(value = "beans") final List<BeanJpaEntity> beans);

    @Query("""
        SELECT b FROM BeanJpaEntity b
        LEFT JOIN FETCH b.beanFlavors bf
        LEFT JOIN FETCH bf.flavor f
        WHERE b.id = :id
        """)
    Optional<BeanJpaEntity> findByIdWithFlavors(@Param(value = "id") final Long id);

    @Query("SELECT b FROM BeanJpaEntity b")
    List<BeanJpaEntity> findAllBeans(final Pageable pageable);
}
