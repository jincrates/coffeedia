package io.coffeedia.domain.model;

import lombok.Builder;

@Builder
public record BeanDetail(
    Bean bean,
    String memo
) {

}
