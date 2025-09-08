package io.coffeedia.application.usecase.service;

import io.coffeedia.application.port.repository.EquipmentRepositoryPort;
import io.coffeedia.application.usecase.GetAllEquipmentsUseCase;
import io.coffeedia.application.usecase.dto.EquipmentResponse;
import io.coffeedia.application.usecase.dto.EquipmentSearchQuery;
import io.coffeedia.application.usecase.mapper.EquipmentMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class GetAllEquipmentsService implements GetAllEquipmentsUseCase {

    private final EquipmentRepositoryPort repository;
    private final EquipmentMapper equipmentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentResponse> invoke(final EquipmentSearchQuery query) {
        return repository.findAll(query.pageSize()).stream()
            .map(equipmentMapper::toResponse)
            .toList();
    }
}
