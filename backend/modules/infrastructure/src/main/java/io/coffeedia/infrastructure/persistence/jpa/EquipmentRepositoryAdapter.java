package io.coffeedia.infrastructure.persistence.jpa;

import io.coffeedia.application.port.repository.EquipmentRepositoryPort;
import io.coffeedia.domain.model.Equipment;
import io.coffeedia.infrastructure.persistence.jpa.entity.EquipmentJpaEntity;
import io.coffeedia.infrastructure.persistence.jpa.mapper.EquipmentJapMapper;
import io.coffeedia.infrastructure.persistence.jpa.repository.EquipmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class EquipmentRepositoryAdapter implements EquipmentRepositoryPort {

    private final EquipmentJpaRepository repository;

    @Override
    public Equipment save(final Equipment equipment) {
        EquipmentJpaEntity entity = EquipmentJapMapper.toEntity(equipment);
        EquipmentJpaEntity saved = repository.save(entity);
        return EquipmentJapMapper.toDomain(saved);
    }
}
