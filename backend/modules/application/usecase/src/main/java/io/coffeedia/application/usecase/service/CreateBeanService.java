package io.coffeedia.application.usecase.service;

import io.coffeedia.application.port.repository.BeanRepositoryPort;
import io.coffeedia.application.port.repository.FlavorRepositoryPort;
import io.coffeedia.application.usecase.CreateBeanUseCase;
import io.coffeedia.application.usecase.dto.BeanResponse;
import io.coffeedia.application.usecase.dto.CreateBeanCommand;
import io.coffeedia.application.usecase.mapper.BeanMapper;
import io.coffeedia.domain.event.BeanEvent.BeanCreated;
import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.model.Flavor;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public BeanResponse invoke(final CreateBeanCommand command) {
        List<Flavor> flavors = validateFlavors(command.flavorIds());

        Bean bean = BeanMapper.toDomain(command, flavors);
        Bean created = beanRepository.create(bean);

        eventPublisher.publishEvent(
            BeanCreated.builder()
                .beanId(created.id())
                .issuedAt(LocalDateTime.now())
                .build()
        );

        return BeanMapper.toResponse(created);
    }

    private List<Flavor> validateFlavors(final List<Long> flavorIds) {
        if (flavorIds == null || flavorIds.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> uniqueIds = new HashSet<>(flavorIds);
        List<Flavor> flavors = flavorRepository.findAllByIds(uniqueIds);

        if (flavors.size() != uniqueIds.size()) {
            throw new IllegalArgumentException("하나 이상의 유효하지 않은 flavor ID가 포함되어 있습니다.");
        }

        return flavors;
    }
}
