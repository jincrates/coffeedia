package io.coffeedia.infrastructure.persistence.jdbc;

import io.coffeedia.application.port.repository.FlavorRepositoryPort;
import io.coffeedia.domain.model.Flavor;
import io.coffeedia.infrastructure.persistence.jdbc.mapper.FlavorJdbcMapper;
import io.coffeedia.infrastructure.persistence.jdbc.repository.FlavorJdbcRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class FlavorRepositoryAdapter implements FlavorRepositoryPort {

    private final FlavorJdbcRepository repository;

    @Override
    public List<Flavor> getAllFlavors(final List<Long> ids) {
        return repository.findAllById(ids).stream()
            .map(FlavorJdbcMapper::toDomain)
            .toList();
    }
}
