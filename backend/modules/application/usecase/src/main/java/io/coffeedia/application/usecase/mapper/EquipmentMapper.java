package io.coffeedia.application.usecase.mapper;

import io.coffeedia.application.usecase.dto.CreateEquipmentCommand;
import io.coffeedia.application.usecase.dto.EquipmentResponse;
import io.coffeedia.application.usecase.dto.UpdateEquipmentCommand;
import io.coffeedia.domain.model.Equipment;
import org.springframework.stereotype.Component;

@Component
public class EquipmentMapper {

    public Equipment toDomain(final CreateEquipmentCommand command) {
        return Equipment.builder()
            .userId(command.userId())
            .type(command.type())
            .name(command.name())
            .brand(command.brand())
            .status(command.status())
            .description(command.description())
            .buyDate(command.buyDate())
            .buyUrl(command.buyUrl())
            .build();
    }

    public Equipment toDomain(final UpdateEquipmentCommand command, final Equipment existingEquipment) {
        return Equipment.builder()
            .id(existingEquipment.id())
            .userId(command.userId())
            .type(command.type())
            .name(command.name())
            .brand(command.brand())
            .status(existingEquipment.status())  // 상태는 기존 것 유지
            .description(command.description())
            .buyDate(command.buyDate())
            .buyUrl(command.buyUrl())
            .createdAt(existingEquipment.createdAt())
            .updatedAt(existingEquipment.updatedAt())
            .build();
    }

    public EquipmentResponse toResponse(final Equipment equipment) {
        return EquipmentResponse.builder()
            .id(equipment.id())
            .type(equipment.type())
            .name(equipment.name())
            .brand(equipment.brand())
            .status(equipment.status())
            .description(equipment.description())
            .buyDate(equipment.buyDate())
            .buyUrl(equipment.buyUrl())
            .createdAt(equipment.createdAt())
            .updatedAt(equipment.updatedAt())
            .build();
    }
}
