package io.coffeedia.infrastructure.persistence.jpa.repository;

import io.coffeedia.infrastructure.persistence.jpa.entity.EquipmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentJpaRepository extends JpaRepository<EquipmentJpaEntity, Long> {

}
