package io.coffeedia.application.usecase.service;

import io.coffeedia.application.port.repository.BeanRepositoryPort;
import io.coffeedia.application.usecase.DeleteBeanUseCase;
import io.coffeedia.application.usecase.dto.DeleteBeanCommand;
import io.coffeedia.application.usecase.dto.DeleteBeanResponse;
import io.coffeedia.domain.event.BeanEvent.BeanDeleted;
import io.coffeedia.domain.exception.AccessDeniedException;
import io.coffeedia.domain.model.Bean;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class DeleteBeanService implements DeleteBeanUseCase {

    private final BeanRepositoryPort repository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public DeleteBeanResponse invoke(final DeleteBeanCommand command) {
        Bean bean = repository.findById(command.id())
            .orElseThrow(() -> new IllegalArgumentException(
                "원두를 찾을 수 없습니다. (id: " + command.id() + ")"
            ));

        if (!bean.userId().equals(command.userId())) {
            throw new AccessDeniedException(
                "권한이 없습니다. (id: " + command.id() + ")"
            );
        }

        repository.delete(command.id());

        eventPublisher.publishEvent(BeanDeleted.builder()
            .beanId(command.id())
            .issuedAt(LocalDateTime.now())
            .build()
        );

        return new DeleteBeanResponse(command.id());
    }
}
