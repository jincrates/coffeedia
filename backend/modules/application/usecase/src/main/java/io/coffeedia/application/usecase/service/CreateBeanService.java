package io.coffeedia.application.usecase.service;

import io.coffeedia.application.port.repository.BeanRepositoryPort;
import io.coffeedia.application.port.repository.FlavorRepositoryPort;
import io.coffeedia.application.usecase.CreateBeanUseCase;
import io.coffeedia.application.usecase.dto.CreateBeanCommand;
import io.coffeedia.application.usecase.dto.CreateBeanResponse;
import io.coffeedia.application.usecase.mapper.BeanMapper;
import io.coffeedia.domain.event.BeanEvent.BeanCreated;
import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.model.Flavor;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class CreateBeanService implements CreateBeanUseCase {

    private final BeanRepositoryPort beanRepository;
    private final FlavorRepositoryPort flavorRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public CreateBeanResponse invoke(final CreateBeanCommand command) {
        List<Flavor> flavors = Collections.emptyList();

        if (!command.flavorIds().isEmpty()) {
            flavors = flavorRepository.getAllFlavors(command.flavorIds());
        }

        Bean bean = BeanMapper.toDomain(command, flavors);

        Bean created = beanRepository.create(bean);

        eventPublisher.publishEvent(
            BeanCreated.builder()
                .beanId(created.id())
                .build()
        );

        return BeanMapper.toResponse(created);
    }
}
