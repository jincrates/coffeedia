package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.EquipmentResponse;
import io.coffeedia.application.usecase.dto.EquipmentSearchQuery;
import java.util.List;

public interface GetAllEquipmentsUseCase {

    List<EquipmentResponse> invoke(EquipmentSearchQuery query);
}
