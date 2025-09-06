package io.coffeedia.application.port.repository;

import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.vo.PageSize;
import io.coffeedia.domain.vo.SortType;
import java.util.List;
import java.util.Optional;

public interface BeanRepositoryPort {

    Bean create(Bean bean);

    List<Bean> createAll(List<Bean> beans);

    Bean update(Bean bean);

    void deleteAll();

    Optional<Bean> findById(Long beanId);

    List<Bean> findAll(PageSize pageSize, List<SortType> sorts);

    void delete(Long beanId);
}
