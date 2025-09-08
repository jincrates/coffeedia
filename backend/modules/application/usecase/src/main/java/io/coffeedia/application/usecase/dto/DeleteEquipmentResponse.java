package io.coffeedia.application.usecase.dto;

import lombok.Builder;

@Builder
public record DeleteEquipmentResponse(
    Long equipmentId,
    String message
) {
    
    public static DeleteEquipmentResponse of(Long equipmentId) {
        return DeleteEquipmentResponse.builder()
            .equipmentId(equipmentId)
            .message("장비가 성공적으로 삭제되었습니다.")
            .build();
    }
}
