package io.coffeedia.infrastructure.persistence.jdbc;

import io.coffeedia.application.port.repository.BeanRepositoryPort;
import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.model.Flavor;
import io.coffeedia.domain.vo.BeanSortType;
import io.coffeedia.domain.vo.PageSize;
import io.coffeedia.infrastructure.persistence.jdbc.entity.BeanJdbcEntity;
import io.coffeedia.infrastructure.persistence.jdbc.mapper.BeanJdbcMapper;
import io.coffeedia.infrastructure.persistence.jdbc.mapper.FlavorJdbcMapper;
import io.coffeedia.infrastructure.persistence.jdbc.repository.BeanJdbcRepository;
import io.coffeedia.infrastructure.persistence.jdbc.repository.FlavorJdbcRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class BeanRepositoryAdapter implements BeanRepositoryPort {

    private final BeanJdbcRepository beanRepository;
    private final FlavorJdbcRepository flavorRepository;

    @Override
    public Bean create(final Bean bean) {
        BeanJdbcEntity entity = BeanJdbcMapper.toEntity(bean);
        BeanJdbcEntity saved = beanRepository.save(entity);

        List<Flavor> flavors = findFlavorsByIds(
            bean.flavors().stream()
                .map(Flavor::id)
                .toList()
        );

        return BeanJdbcMapper.toDomain(saved, flavors);
    }

    @Override
    public List<Bean> createAll(final List<Bean> beans) {
        List<BeanJdbcEntity> entity = BeanJdbcMapper.toEntity(beans);
        List<BeanJdbcEntity> saved = beanRepository.saveAll(entity);
//        return BeanJdbcMapper.toDomain(saved, flavorMap);
        return null;
    }

    @Override
    public Bean update(final Bean bean) {
        if (bean.id() == null) {
            throw new IllegalArgumentException("원두 ID는 필수입니다.");
        }
        return create(bean);
    }

    @Override
    public void deleteAll() {
        beanRepository.deleteAll();
    }

    @Override
    public Optional<Bean> findById(final Long beanId) {
        return beanRepository.findById(beanId)
            .map(it -> {
                List<Flavor> flavors = findFlavorsByIds(it.flavorIds());
                return BeanJdbcMapper.toDomain(it, flavors);
            });
    }

    @Override
    public List<Bean> findAll(final PageSize pageSize, final List<BeanSortType> sorts) {
        Pageable pageable = PageRequest.of(
            pageSize.page(),
            pageSize.size() + 1,  // 다음 페이지가 있는지 확인하기 위해 +1
            toSort(sorts)
        );
        List<BeanJdbcEntity> beans = beanRepository.findAll(pageable).getContent();

        if (beans.isEmpty()) {
            return Collections.emptyList();
        }

//        List<Long> beanIds = beans.stream().map(BeanJdbcEntity::id).toList();
//        Map<Long, List<Flavor>> flavorsByBeanId = findFlavorsByBeanIds(beanIds);
//
//        return beans.stream()
//            .map(entity -> {
//                List<Flavor> flavors = flavorsByBeanId.getOrDefault(entity.id(), List.of());
//                return BeanJdbcMapper.toDomain(entity, flavors);
//            })
//            .toList();
        return null;
    }

    private List<Flavor> findFlavorsByIds(final List<Long> flavorIds) {
        return flavorRepository.findAllById(flavorIds).stream()
            .map(FlavorJdbcMapper::toDomain)
            .toList();
    }

    private Sort toSort(final List<BeanSortType> sorts) {
        List<Sort.Order> orders = sorts.stream()
            .map(sort -> new Sort.Order(
                Sort.Direction.fromString(sort.getDirection()), sort.getField())
            )
            .toList();
        return Sort.by(orders);
    }
}
