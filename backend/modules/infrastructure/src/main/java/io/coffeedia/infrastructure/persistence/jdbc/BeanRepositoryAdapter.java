package io.coffeedia.infrastructure.persistence.jdbc;

import io.coffeedia.application.port.repository.BeanRepositoryPort;
import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.model.Flavor;
import io.coffeedia.infrastructure.persistence.jdbc.entity.BeanFlavorJdbcEntity;
import io.coffeedia.infrastructure.persistence.jdbc.entity.BeanJdbcEntity;
import io.coffeedia.infrastructure.persistence.jdbc.mapper.BeanJdbcMapper;
import io.coffeedia.infrastructure.persistence.jdbc.repository.BeanFlavorJdbcRepository;
import io.coffeedia.infrastructure.persistence.jdbc.repository.BeanJdbcRepository;
import io.coffeedia.infrastructure.persistence.jdbc.repository.FlavorJdbcRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class BeanRepositoryAdapter implements BeanRepositoryPort {

    private final BeanJdbcRepository beanRepository;
    private final FlavorJdbcRepository flavorRepository;
    private final BeanFlavorJdbcRepository beanFlavorRepository;

    @Override
    public Bean create(final Bean bean) {
        BeanJdbcEntity entity = BeanJdbcMapper.toEntity(bean);
        BeanJdbcEntity saved = beanRepository.save(entity);
        beanFlavorRepository.saveAll(BeanJdbcMapper.toEntity(saved, bean.flavors()));

        return BeanJdbcMapper.toDomain(saved, bean.flavors());
    }

    @Override
    public Bean update(final Bean bean) {
        if (bean.id() == null) {
            throw new IllegalArgumentException("원두 ID는 필수입니다.");
        }
        BeanJdbcEntity entity = BeanJdbcMapper.toEntity(bean);
        BeanJdbcEntity saved = beanRepository.save(entity);
        return BeanJdbcMapper.toDomain(saved, bean.flavors());
    }

    @Override
    public Optional<Bean> findById(final Long beanId) {
        List<Flavor> flavors = findFlavors(beanId);
        return beanRepository.findById(beanId)
            .map(it -> BeanJdbcMapper.toDomain(it, flavors));
    }

    @Override
    public void deleteAll() {
        beanFlavorRepository.deleteAll();
        beanRepository.deleteAll();
    }

    private List<Flavor> findFlavors(final Long beanId) {
        List<Long> flavorIds = findFlavorIds(beanId);
        return flavorRepository.findAllById(flavorIds).stream()
            .map(BeanJdbcMapper::toDomain)
            .toList();
    }

    private List<Long> findFlavorIds(final Long beanId) {
        List<BeanFlavorJdbcEntity> beanFlavors = beanFlavorRepository.findAllByBeanId(beanId);
        return beanFlavors.stream()
            .map(BeanFlavorJdbcEntity::flavorId)
            .toList();
    }
}
