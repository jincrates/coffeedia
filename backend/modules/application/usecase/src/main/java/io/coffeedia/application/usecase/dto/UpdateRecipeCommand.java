package io.coffeedia.application.usecase.dto;

import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.CategoryType;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.With;

@Builder
public record UpdateRecipeCommand(
    @With
    Long recipeId,
    @With
    Long userId,
    CategoryType category,
    String title,
    String thumbnailUrl,
    String description,
    Integer serving,
    List<String> tags,
    List<UpdateIngredientCommand> ingredients,
    List<UpdateStepCommand> steps,
    String tips,
    ActiveStatus status
) {

    @Builder
    public record UpdateIngredientCommand(
        String name,
        BigDecimal amount,
        String unit,
        String buyUrl
    ) {

    }

    @Builder
    public record UpdateStepCommand(
        String description,
        String imageUrl
    ) {

    }
}
