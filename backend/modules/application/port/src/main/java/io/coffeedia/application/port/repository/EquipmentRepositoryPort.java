package io.coffeedia.application.port.repository;

import io.coffeedia.domain.model.Equipment;
import io.coffeedia.domain.vo.PageSize;
import java.util.List;

public interface EquipmentRepositoryPort {

    Equipment save(Equipment equipment);

    List<Equipment> findAll(PageSize pageSize);
}
