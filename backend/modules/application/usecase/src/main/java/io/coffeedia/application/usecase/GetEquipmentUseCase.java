package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.EquipmentResponse;

public interface GetEquipmentUseCase {
    EquipmentResponse invoke(Long equipmentId);
}
