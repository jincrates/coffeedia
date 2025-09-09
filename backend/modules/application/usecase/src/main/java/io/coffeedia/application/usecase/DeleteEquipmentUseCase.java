package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.DeleteEquipmentCommand;
import io.coffeedia.application.usecase.dto.DeleteEquipmentResponse;

public interface DeleteEquipmentUseCase {
    DeleteEquipmentResponse invoke(DeleteEquipmentCommand command);
}
