package io.coffeedia.application.usecase.service;

import io.coffeedia.application.port.repository.EquipmentRepositoryPort;
import io.coffeedia.application.usecase.GetEquipmentUseCase;
import io.coffeedia.application.usecase.dto.EquipmentResponse;
import io.coffeedia.application.usecase.mapper.EquipmentMapper;
import io.coffeedia.domain.exception.EquipmentNotFoundException;
import io.coffeedia.domain.model.Equipment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetEquipmentService implements GetEquipmentUseCase {

    private final EquipmentRepositoryPort equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    @Override
    public EquipmentResponse invoke(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
            .orElseThrow(() -> new EquipmentNotFoundException(equipmentId));
            
        return equipmentMapper.toResponse(equipment);
    }
}
