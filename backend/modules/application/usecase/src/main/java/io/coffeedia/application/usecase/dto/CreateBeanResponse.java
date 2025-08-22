package io.coffeedia.application.usecase.dto;

import lombok.Builder;

@Builder
public record CreateBeanResponse(
    Long beanId
) {

}
