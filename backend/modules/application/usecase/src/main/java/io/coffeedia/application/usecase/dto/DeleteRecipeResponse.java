package io.coffeedia.application.usecase.dto;

import lombok.Builder;

@Builder
public record DeleteRecipeResponse(
    Long recipeId
) {

    public static DeleteRecipeResponse of(Long recipeId) {
        return DeleteRecipeResponse.builder()
            .recipeId(recipeId)
            .build();
    }
}
