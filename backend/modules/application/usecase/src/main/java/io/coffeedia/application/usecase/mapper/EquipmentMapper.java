package io.coffeedia.application.usecase.mapper;

import io.coffeedia.application.usecase.dto.CreateEquipmentCommand;
import io.coffeedia.application.usecase.dto.EquipmentResponse;
import io.coffeedia.domain.model.Equipment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EquipmentMapper {

    public static Equipment toDomain(final CreateEquipmentCommand command) {
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

    public static EquipmentResponse toResponse(final Equipment equipment) {
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
