package io.coffeedia.application.usecase.dto;

import lombok.Builder;

@Builder
public record DeleteRecipeCommand(
    Long recipeId,
    Long userId
) {

    public static DeleteRecipeCommand of(Long recipeId, Long userId) {
        return DeleteRecipeCommand.builder()
            .recipeId(recipeId)
            .userId(userId)
            .build();
    }
}
