package io.coffeedia.infrastructure.persistence.jdbc;

import io.coffeedia.application.port.repository.BeanRepositoryPort;
import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.model.Flavor;
import io.coffeedia.domain.vo.BeanSortType;
import io.coffeedia.domain.vo.PageSize;
import io.coffeedia.infrastructure.persistence.jdbc.entity.BeanFlavorJdbcEntity;
import io.coffeedia.infrastructure.persistence.jdbc.entity.BeanJdbcEntity;
import io.coffeedia.infrastructure.persistence.jdbc.entity.FlavorJdbcEntity;
import io.coffeedia.infrastructure.persistence.jdbc.mapper.BeanJdbcMapper;
import io.coffeedia.infrastructure.persistence.jdbc.repository.BeanFlavorJdbcRepository;
import io.coffeedia.infrastructure.persistence.jdbc.repository.BeanJdbcRepository;
import io.coffeedia.infrastructure.persistence.jdbc.repository.FlavorJdbcRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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

        List<Long> beanIds = beans.stream().map(BeanJdbcEntity::id).toList();
        Map<Long, List<Flavor>> flavorsByBeanId = findFlavorsByBeanIds(beanIds);

        return beans.stream()
            .map(entity -> {
                List<Flavor> flavors = flavorsByBeanId.getOrDefault(entity.id(), List.of());
                return BeanJdbcMapper.toDomain(entity, flavors);
            })
            .toList();
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

    private Map<Long, List<Flavor>> findFlavorsByBeanIds(final List<Long> beanIds) {
        if (beanIds.isEmpty()) {
            return Map.of();
        }

        // 1. Bean-Flavor 관계 조회
        List<BeanFlavorJdbcEntity> beanFlavors = beanFlavorRepository.findAllByBeanIdIn(beanIds);

        if (beanFlavors.isEmpty()) {
            return beanIds.stream()
                .collect(Collectors.toMap(beanId -> beanId, beanId -> List.of()));
        }

        // 2. 고유한 Flavor ID들 추출
        List<Long> flavorIds = beanFlavors.stream()
            .map(BeanFlavorJdbcEntity::flavorId)
            .distinct()
            .toList();

        // 3. Flavor 엔티티들 배치 조회
        Map<Long, Flavor> flavorMap = flavorRepository.findAllById(flavorIds)
            .stream()
            .collect(Collectors.toMap(
                FlavorJdbcEntity::id,
                BeanJdbcMapper::toDomain
            ));

        // 4. Bean ID별로 Flavor 리스트 그룹핑
        Map<Long, List<Flavor>> result = beanFlavors.stream()
            .collect(Collectors.groupingBy(
                BeanFlavorJdbcEntity::beanId,
                Collectors.mapping(
                    beanFlavor -> flavorMap.get(beanFlavor.flavorId()),
                    Collectors.filtering(Objects::nonNull, Collectors.toList())
                )
            ));

        // 5. Flavor가 없는 Bean들도 빈 리스트로 포함
        beanIds.forEach(beanId -> result.putIfAbsent(beanId, List.of()));

        return result;
    }


    private Sort toSort(final List<BeanSortType> sorts) {
        return sorts.stream()
            .map(it -> toSort(it.getField(), it.getDirection()))
            .reduce(Sort.unsorted(), Sort::and);
    }

    private Sort toSort(final String field, final String direction) {
        Sort.Direction sortDirection = "asc".equals(direction)
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;

        return Sort.by(sortDirection, field);
    }
}
