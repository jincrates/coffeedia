package io.coffeedia.application.usecase.service;

import io.coffeedia.application.port.repository.EquipmentRepositoryPort;
import io.coffeedia.application.usecase.UpdateEquipmentUseCase;
import io.coffeedia.application.usecase.dto.EquipmentResponse;
import io.coffeedia.application.usecase.dto.UpdateEquipmentCommand;
import io.coffeedia.application.usecase.mapper.EquipmentMapper;
import io.coffeedia.domain.exception.EquipmentNotFoundException;
import io.coffeedia.domain.model.Equipment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateEquipmentService implements UpdateEquipmentUseCase {

    private final EquipmentRepositoryPort equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    @Override
    public EquipmentResponse invoke(UpdateEquipmentCommand command) {
        // 기존 장비 조회
        Equipment existingEquipment = equipmentRepository.findById(command.equipmentId())
            .orElseThrow(() -> new EquipmentNotFoundException(command.equipmentId()));

        // 업데이트할 장비 도메인 객체 생성
        Equipment updatedEquipment = equipmentMapper.toDomain(command, existingEquipment);
        
        // 저장
        Equipment savedEquipment = equipmentRepository.save(updatedEquipment);
        
        return equipmentMapper.toResponse(savedEquipment);
    }
}
