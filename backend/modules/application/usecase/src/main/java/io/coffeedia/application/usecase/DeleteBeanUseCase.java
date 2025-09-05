package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.DeleteBeanCommand;
import io.coffeedia.application.usecase.dto.DeleteBeanResponse;

public interface DeleteBeanUseCase {

    DeleteBeanResponse invoke(final DeleteBeanCommand command);
}
