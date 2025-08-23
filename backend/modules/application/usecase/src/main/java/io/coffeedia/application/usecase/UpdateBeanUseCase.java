package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.BeanResponse;
import io.coffeedia.application.usecase.dto.UpdateBeanCommand;

public interface UpdateBeanUseCase {

    BeanResponse invoke(final UpdateBeanCommand command);
}
