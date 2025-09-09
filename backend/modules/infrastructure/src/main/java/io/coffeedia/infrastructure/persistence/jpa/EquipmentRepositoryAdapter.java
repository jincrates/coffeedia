package io.coffeedia.infrastructure.persistence.jpa;

import io.coffeedia.application.port.repository.EquipmentRepositoryPort;
import io.coffeedia.domain.model.Equipment;
import io.coffeedia.domain.vo.PageSize;
import io.coffeedia.infrastructure.persistence.jpa.entity.EquipmentJpaEntity;
import io.coffeedia.infrastructure.persistence.jpa.mapper.EquipmentJpaMapper;
import io.coffeedia.infrastructure.persistence.jpa.repository.EquipmentJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class EquipmentRepositoryAdapter implements EquipmentRepositoryPort {

    private final EquipmentJpaRepository repository;

    @Override
    public Equipment save(final Equipment equipment) {
        EquipmentJpaEntity entity = EquipmentJpaMapper.toEntity(equipment);
        EquipmentJpaEntity saved = repository.save(entity);
        return EquipmentJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Equipment> findById(final Long equipmentId) {
        return repository.findById(equipmentId)
            .map(EquipmentJpaMapper::toDomain);
    }

    @Override
    public List<Equipment> findAll(final PageSize pageSize) {
        Pageable pageable = PageRequest.of(
            pageSize.page(),
            pageSize.size() + 1  // 다음 페이지가 있는지 확인하기 위해 +1
        );
        return repository.findAllEquipments(pageable).stream()
            .map(EquipmentJpaMapper::toDomain)
            .toList();
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
