package io.coffeedia.infrastructure.persistence.jpa;

import io.coffeedia.application.port.repository.FlavorRepositoryPort;
import io.coffeedia.domain.model.Flavor;
import io.coffeedia.infrastructure.persistence.jpa.mapper.FlavorJpaMapper;
import io.coffeedia.infrastructure.persistence.jpa.repository.FlavorJpaRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class FlavorRepositoryAdapter implements FlavorRepositoryPort {

    private final FlavorJpaRepository repository;

    @Override
    public List<Flavor> findAllByIds(final Set<Long> ids) {
        return repository.findAllById(ids).stream()
            .map(FlavorJpaMapper::toDomain)
            .toList();
    }
}
