package io.coffeedia.application.usecase.service;

import io.coffeedia.application.port.repository.EquipmentRepositoryPort;
import io.coffeedia.application.usecase.DeleteEquipmentUseCase;
import io.coffeedia.application.usecase.dto.DeleteEquipmentCommand;
import io.coffeedia.application.usecase.dto.DeleteEquipmentResponse;
import io.coffeedia.domain.exception.EquipmentNotFoundException;
import io.coffeedia.domain.model.Equipment;
import io.coffeedia.domain.vo.ActiveStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteEquipmentService implements DeleteEquipmentUseCase {

    private final EquipmentRepositoryPort equipmentRepository;

    @Override
    public DeleteEquipmentResponse invoke(DeleteEquipmentCommand command) {
        // 기존 장비 조회
        Equipment existingEquipment = equipmentRepository.findById(command.equipmentId())
            .orElseThrow(() -> new EquipmentNotFoundException(command.equipmentId()));

        // 소프트 삭제 (상태를 INACTIVE로 변경)
        Equipment deletedEquipment = Equipment.builder()
            .id(existingEquipment.id())
            .userId(existingEquipment.userId())
            .type(existingEquipment.type())
            .name(existingEquipment.name())
            .brand(existingEquipment.brand())
            .status(ActiveStatus.INACTIVE)  // 상태만 변경
            .description(existingEquipment.description())
            .buyDate(existingEquipment.buyDate())
            .buyUrl(existingEquipment.buyUrl())
            .createdAt(existingEquipment.createdAt())
            .updatedAt(existingEquipment.updatedAt())
            .build();

        equipmentRepository.save(deletedEquipment);
        
        return DeleteEquipmentResponse.of(command.equipmentId());
    }
}
