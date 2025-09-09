package io.coffeedia.application.usecase.dto;

public record DeleteEquipmentCommand(
    Long equipmentId,
    Long userId
) {
    
    public static DeleteEquipmentCommand of(Long equipmentId, Long userId) {
        return new DeleteEquipmentCommand(equipmentId, userId);
    }
}
