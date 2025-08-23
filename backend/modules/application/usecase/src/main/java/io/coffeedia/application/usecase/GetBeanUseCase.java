package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.BeanResponse;

public interface GetBeanUseCase {

    BeanResponse invoke(final Long beanId);
}
