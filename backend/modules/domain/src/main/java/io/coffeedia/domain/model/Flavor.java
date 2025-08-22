package io.coffeedia.domain.model;

import lombok.Builder;

@Builder
public record Flavor(
    Long id,
    String name
) {

}
