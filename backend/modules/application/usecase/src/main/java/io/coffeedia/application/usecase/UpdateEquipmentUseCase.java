package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.EquipmentResponse;
import io.coffeedia.application.usecase.dto.UpdateEquipmentCommand;

public interface UpdateEquipmentUseCase {
    EquipmentResponse invoke(UpdateEquipmentCommand command);
}
