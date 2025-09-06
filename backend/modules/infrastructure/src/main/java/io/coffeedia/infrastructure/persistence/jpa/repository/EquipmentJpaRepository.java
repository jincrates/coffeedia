package io.coffeedia.infrastructure.persistence.jpa.repository;

import io.coffeedia.infrastructure.persistence.jpa.entity.EquipmentJpaEntity;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EquipmentJpaRepository extends JpaRepository<EquipmentJpaEntity, Long> {

    @Query("SELECT e FROM EquipmentJpaEntity e")
    List<EquipmentJpaEntity> findAllEquipments(Pageable pageable);
}
