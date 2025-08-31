package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.CreateEquipmentCommand;
import io.coffeedia.application.usecase.dto.EquipmentResponse;

public interface CreateEquipmentUseCase {

    EquipmentResponse invoke(final CreateEquipmentCommand command);
}
