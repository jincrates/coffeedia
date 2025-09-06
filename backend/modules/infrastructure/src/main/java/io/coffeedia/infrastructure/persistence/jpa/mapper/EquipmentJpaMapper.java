package io.coffeedia.infrastructure.persistence.jpa.mapper;

import io.coffeedia.domain.model.Equipment;
import io.coffeedia.infrastructure.persistence.jpa.entity.EquipmentJpaEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EquipmentJpaMapper {

    public static EquipmentJpaEntity toEntity(final Equipment equipment) {
        return EquipmentJpaEntity.builder()
            .id(equipment.id())
            .userId(equipment.userId())
            .type(equipment.type())
            .name(equipment.name())
            .brand(equipment.brand())
            .status(equipment.status())
            .description(equipment.description())
            .buyDate(equipment.buyDate())
            .buyUrl(equipment.buyUrl())
            .build();
    }

    public static Equipment toDomain(final EquipmentJpaEntity equipment) {
        return Equipment.builder()
            .id(equipment.getId())
            .userId(equipment.getUserId())
            .type(equipment.getType())
            .name(equipment.getName())
            .brand(equipment.getBrand())
            .status(equipment.getStatus())
            .description(equipment.getDescription())
            .buyDate(equipment.getBuyDate())
            .buyUrl(equipment.getBuyUrl())
            .createdAt(equipment.getCreatedAt())
            .updatedAt(equipment.getUpdatedAt())
            .build();
    }
}
