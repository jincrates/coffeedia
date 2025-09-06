package io.coffeedia.infrastructure.persistence.jpa;

import io.coffeedia.application.port.repository.BeanRepositoryPort;
import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.vo.PageSize;
import io.coffeedia.domain.vo.SortType;
import io.coffeedia.infrastructure.persistence.jpa.entity.BeanJpaEntity;
import io.coffeedia.infrastructure.persistence.jpa.mapper.BeanJpaMapper;
import io.coffeedia.infrastructure.persistence.jpa.repository.BeanJpaRepository;
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

    private final BeanJpaRepository beanRepository;

    @Override
    public Bean create(final Bean bean) {
        BeanJpaEntity entity = BeanJpaMapper.toEntity(bean);
        BeanJpaEntity saved = beanRepository.save(entity);
        return BeanJpaMapper.toDomain(saved);
    }

    @Override
    public List<Bean> createAll(final List<Bean> beans) {
        List<BeanJpaEntity> entity = BeanJpaMapper.toEntity(beans);
        List<BeanJpaEntity> saved = beanRepository.saveAll(entity);
        return BeanJpaMapper.toDomain(saved);
    }

    @Override
    public Bean update(final Bean newBean) {
        Long beanId = newBean.id();

        if (beanId == null) {
            throw new IllegalArgumentException("원두 ID는 필수입니다.");
        }

        BeanJpaEntity bean = beanRepository.findById(beanId)
            .orElseThrow(() -> new IllegalArgumentException(
                "원두를 찾을 수 없습니다. (id: " + beanId + ")"
            ));
        bean.update(newBean);  // 더티체킹으로 변경
        return BeanJpaMapper.toDomain(bean);
    }

    @Override
    public void deleteAll() {
        beanRepository.deleteAll();
    }

    @Override
    public Optional<Bean> findById(final Long beanId) {
        return beanRepository.findByIdWithFlavors(beanId)
            .map(BeanJpaMapper::toDomain);
    }

    @Override
    public List<Bean> findAll(final PageSize pageSize, final List<SortType> sorts) {
        Pageable pageable = PageRequest.of(
            pageSize.page(),
            pageSize.size() + 1,  // 다음 페이지가 있는지 확인하기 위해 +1
            toSort(sorts)
        );
        List<BeanJpaEntity> beans = beanRepository.findAllBeans(pageable);
        return beanRepository.findAllWithFlavors(beans).stream()
            .map(BeanJpaMapper::toDomain)
            .toList();
    }

    @Override
    public void delete(final Long beanId) {
        beanRepository.deleteById(beanId);
    }

    private Sort toSort(final List<SortType> sorts) {
        List<Sort.Order> orders = sorts.stream()
            .map(sort -> new Sort.Order(
                Sort.Direction.fromString(sort.getDirection()), sort.getField())
            )
            .toList();
        return Sort.by(orders);
    }
}
