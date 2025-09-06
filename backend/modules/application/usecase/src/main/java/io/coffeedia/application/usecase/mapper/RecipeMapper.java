package io.coffeedia.application.usecase.mapper;

import io.coffeedia.application.usecase.dto.CreateRecipeCommand;
import io.coffeedia.application.usecase.dto.CreateRecipeCommand.CreateIngredientCommand;
import io.coffeedia.application.usecase.dto.CreateRecipeCommand.CreateStepCommand;
import io.coffeedia.application.usecase.dto.RecipeResponse;
import io.coffeedia.domain.model.Ingredient;
import io.coffeedia.domain.model.Recipe;
import io.coffeedia.domain.model.RecipeStep;
import io.coffeedia.domain.vo.ActiveStatus;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeMapper {

    public static Recipe toDomain(final CreateRecipeCommand command) {
        return Recipe.builder()
            .userId(command.userId())
            .category(command.category())
            .title(command.title())
            .thumbnailUrl(command.thumbnailUrl())
            .description(command.description())
            .serving(command.serving())
            .tags(command.tags())
            .ingredients(toIngredients(command.ingredients()))
            .steps(toSteps(command.steps()))
            .tips(command.tips())
            .status(ActiveStatus.ACTIVE)
            .build();
    }

    public static List<Ingredient> toIngredients(final List<CreateIngredientCommand> commands) {
        return commands.stream()
            .map(it -> Ingredient.builder()
                .name(it.name())
                .amount(it.amount())
                .unit(it.unit())
                .buyUrl(it.buyUrl())
                .build())
            .toList();
    }

    public static List<RecipeStep> toSteps(final List<CreateStepCommand> commands) {
        if (commands == null || commands.isEmpty()) {
            return Collections.emptyList();
        }

        return IntStream.range(0, commands.size())
            .mapToObj(i -> RecipeStep.builder()
                .sortOrder(i + 1)
                .imageUrl(commands.get(i).imageUrl())
                .description(commands.get(i).description())
                .build())
            .toList();
    }

    public static RecipeResponse toResponse(final Recipe recipe) {
        return RecipeResponse.builder()
            .id(recipe.id())
            .userId(recipe.userId())
            .category(recipe.category())
            .title(recipe.title())
            .thumbnailUrl(recipe.thumbnailUrl())
            .description(recipe.description())
            .serving(recipe.serving())
            .tags(recipe.tags())
            .ingredients(recipe.ingredients())
            .steps(recipe.steps())
            .tips(recipe.tips())
            .status(recipe.status())
            .createdAt(recipe.createdAt())
            .updatedAt(recipe.updatedAt())
            .build();
    }
}
