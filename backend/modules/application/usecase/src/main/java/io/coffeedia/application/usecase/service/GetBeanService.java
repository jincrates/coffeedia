package io.coffeedia.application.usecase.service;

import io.coffeedia.application.port.repository.BeanRepositoryPort;
import io.coffeedia.application.usecase.GetBeanUseCase;
import io.coffeedia.application.usecase.dto.BeanResponse;
import io.coffeedia.application.usecase.mapper.BeanMapper;
import io.coffeedia.domain.model.Bean;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class GetBeanService implements GetBeanUseCase {

    private final BeanRepositoryPort repository;

    @Override
    @Transactional(readOnly = true)
    public BeanResponse invoke(final Long beanId) {
        Bean bean = repository.findById(beanId)
            .orElseThrow(() -> new IllegalArgumentException("원두를 찾을 수 없습니다. (" + beanId + ")"));
        return BeanMapper.toResponse(bean);
    }
}
