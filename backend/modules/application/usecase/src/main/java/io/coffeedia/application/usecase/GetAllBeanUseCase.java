package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.BeanResponse;
import io.coffeedia.application.usecase.dto.BeanSearchQuery;
import java.util.List;

public interface GetAllBeanUseCase {

    List<BeanResponse> invoke(final BeanSearchQuery query);
}
