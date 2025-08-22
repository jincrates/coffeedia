package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.CreateBeanCommand;
import io.coffeedia.application.usecase.dto.CreateBeanResponse;

public interface CreateBeanUseCase {

    CreateBeanResponse invoke(CreateBeanCommand command);
}
