package io.coffeedia.application.usecase.service;

import io.coffeedia.application.port.repository.BeanRepositoryPort;
import io.coffeedia.application.usecase.UpdateBeanUseCase;
import io.coffeedia.application.usecase.dto.BeanResponse;
import io.coffeedia.application.usecase.dto.UpdateBeanCommand;
import io.coffeedia.application.usecase.mapper.BeanMapper;
import io.coffeedia.domain.event.BeanEvent.BeanUpdated;
import io.coffeedia.domain.model.Bean;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class UpdateBeanService implements UpdateBeanUseCase {

    private final BeanRepositoryPort repository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public BeanResponse invoke(final UpdateBeanCommand command) {
        validate(command);

        Bean existing = repository.findById(command.id())
            .orElseThrow(() -> new IllegalArgumentException(
                "원두를 찾을 수 없습니다. (id: " + command.id() + ")"
            ));

        Bean bean = BeanMapper.toDomain(command, existing);
        Bean updated = repository.update(bean);

        eventPublisher.publishEvent(BeanUpdated.builder()
            .beanId(updated.id())
            .issuedAt(LocalDateTime.now())
            .build()
        );

        return BeanMapper.toResponse(updated);
    }

    private void validate(final UpdateBeanCommand command) {
        if (command.id() == null) {
            throw new IllegalArgumentException("원두 ID는 필수입니다.");
        }
        if (command.name() != null && command.name().isBlank()) {
            throw new IllegalArgumentException("원두 이름은 필수입니다.");
        }
        if (command.origin() != null && command.origin().country().isBlank()) {
            throw new IllegalArgumentException("원두 원산지 국가는 필수입니다.");
        }
        if (command.roaster() != null && command.roaster().isBlank()) {
            throw new IllegalArgumentException("원두 로스터는 필수입니다.");
        }
        if (command.grams() != null && command.grams() < 0) {
            throw new IllegalArgumentException("원두 그램은 0g 이상이어야 합니다.");
        }
    }
}
