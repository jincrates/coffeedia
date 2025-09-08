package io.coffeedia.application.port.repository;

import io.coffeedia.domain.model.Equipment;
import io.coffeedia.domain.vo.PageSize;
import java.util.List;
import java.util.Optional;

public interface EquipmentRepositoryPort {

    Equipment save(Equipment equipment);

    Optional<Equipment> findById(Long equipmentId);

    List<Equipment> findAll(PageSize pageSize);
}
