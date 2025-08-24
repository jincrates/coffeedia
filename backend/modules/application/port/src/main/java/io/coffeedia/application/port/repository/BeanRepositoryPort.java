package io.coffeedia.application.port.repository;

import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.vo.BeanSortType;
import io.coffeedia.domain.vo.PageSize;
import java.util.List;
import java.util.Optional;

public interface BeanRepositoryPort {

    Bean create(final Bean bean);

    Bean update(final Bean bean);

    Optional<Bean> findById(final Long beanId);

    void deleteAll();

    List<Bean> findAll(PageSize pageSize, List<BeanSortType> sorts);
}
