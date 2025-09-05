package io.coffeedia.application.usecase.dto;

import lombok.With;

public record DeleteBeanCommand(
    @With
    Long id,
    @With
    Long userId
) {

}
