package io.coffeedia.application.usecase.dto;

import lombok.Builder;

@Builder
public record FlavorResponse(
    Long id,
    String name
) {

}
