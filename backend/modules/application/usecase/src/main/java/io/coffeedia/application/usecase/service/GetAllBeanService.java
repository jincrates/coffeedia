package io.coffeedia.application.usecase.service;

import io.coffeedia.application.port.repository.BeanRepositoryPort;
import io.coffeedia.application.usecase.GetAllBeanUseCase;
import io.coffeedia.application.usecase.dto.BeanResponse;
import io.coffeedia.application.usecase.dto.BeanSearchQuery;
import io.coffeedia.application.usecase.mapper.BeanMapper;
import io.coffeedia.domain.model.Bean;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class GetAllBeanService implements GetAllBeanUseCase {

    private final BeanRepositoryPort repository;

    @Override
    @Transactional(readOnly = true)
    public List<BeanResponse> invoke(final BeanSearchQuery query) {
        List<Bean> beans = repository.findAll(query.pageSize(), query.sort());
        return beans.stream()
            .map(BeanMapper::toResponse)
            .toList();
    }
}
