package io.coffeedia.application.usecase.service;

import io.coffeedia.application.port.repository.EquipmentRepositoryPort;
import io.coffeedia.application.usecase.CreateEquipmentUseCase;
import io.coffeedia.application.usecase.dto.CreateEquipmentCommand;
import io.coffeedia.application.usecase.dto.EquipmentResponse;
import io.coffeedia.application.usecase.mapper.EquipmentMapper;
import io.coffeedia.domain.event.EquipmentEvent.EquipmentCreated;
import io.coffeedia.domain.model.Equipment;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class CreateEquipmentService implements CreateEquipmentUseCase {

    private final EquipmentRepositoryPort repository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public EquipmentResponse invoke(final CreateEquipmentCommand command) {
        Equipment equipment = EquipmentMapper.toDomain(command);
        Equipment saved = repository.save(equipment);

        eventPublisher.publishEvent(
            EquipmentCreated.builder()
                .equipmentId(saved.id())
                .issuedAt(LocalDateTime.now())
                .build()
        );

        return EquipmentMapper.toResponse(saved);
    }
}
