package io.coffeedia.application.port.repository;

import io.coffeedia.domain.model.Equipment;

public interface EquipmentRepositoryPort {

    Equipment save(Equipment equipment);
}
