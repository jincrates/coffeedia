package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.BeanResponse;
import io.coffeedia.application.usecase.dto.CreateBeanCommand;

public interface CreateBeanUseCase {

    BeanResponse invoke(final CreateBeanCommand command);
}
