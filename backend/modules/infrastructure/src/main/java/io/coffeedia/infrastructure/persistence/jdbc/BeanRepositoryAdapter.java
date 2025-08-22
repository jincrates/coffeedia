package io.coffeedia.infrastructure.persistence.jdbc;

import io.coffeedia.application.port.repository.BeanRepositoryPort;
import io.coffeedia.domain.model.Bean;
import io.coffeedia.infrastructure.persistence.jdbc.entity.BeanJdbcEntity;
import io.coffeedia.infrastructure.persistence.jdbc.mapper.BeanJdbcMapper;
import io.coffeedia.infrastructure.persistence.jdbc.repository.BeanFlavorJdbcRepository;
import io.coffeedia.infrastructure.persistence.jdbc.repository.BeanJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class BeanRepositoryAdapter implements BeanRepositoryPort {

    private final BeanJdbcRepository repository;
    private final BeanFlavorJdbcRepository flavorRepository;

    @Override
    public Bean create(final Bean bean) {
        BeanJdbcEntity entity = BeanJdbcMapper.toEntity(bean);
        BeanJdbcEntity saved = repository.save(entity);
        flavorRepository.saveAll(BeanJdbcMapper.toEntity(saved, bean.flavors()));

        return BeanJdbcMapper.toDomain(saved, bean.flavors());
    }
}
