package io.coffeedia.domain.event;

import java.time.LocalDateTime;
import lombok.Builder;

public interface EquipmentEvent extends DomainEvent {

    @Builder
    record EquipmentCreated(
        Long equipmentId,
        LocalDateTime issuedAt
    ) implements EquipmentEvent {

    }
}
