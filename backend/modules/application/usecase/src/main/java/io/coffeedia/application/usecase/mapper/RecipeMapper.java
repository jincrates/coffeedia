package io.coffeedia.application.usecase.mapper;

import io.coffeedia.application.usecase.dto.CreateRecipeCommand;
import io.coffeedia.application.usecase.dto.CreateRecipeCommand.CreateIngredientCommand;
import io.coffeedia.application.usecase.dto.CreateRecipeCommand.CreateStepCommand;
import io.coffeedia.application.usecase.dto.RecipeResponse;
import io.coffeedia.application.usecase.dto.RecipeSummaryResponse;
import io.coffeedia.application.usecase.dto.UpdateRecipeCommand.UpdateIngredientCommand;
import io.coffeedia.application.usecase.dto.UpdateRecipeCommand.UpdateStepCommand;
import io.coffeedia.domain.model.Ingredient;
import io.coffeedia.domain.model.Recipe;
import io.coffeedia.domain.model.RecipeStep;
import io.coffeedia.domain.model.RecipeSummary;
import io.coffeedia.domain.vo.ActiveStatus;
import java.math.BigDecimal;
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
            .ingredients(toIngredientsForCreate(command.ingredients()))
            .steps(toStepsForCreate(command.steps()))
            .tips(command.tips())
            .status(ActiveStatus.ACTIVE)
            .build();
    }

    public static List<Ingredient> toIngredientsForCreate(
        final List<CreateIngredientCommand> commands) {
        return commands.stream()
            .map(it -> {
                validateAmount(it.amount(), it.name());
                return Ingredient.builder()
                    .name(it.name())
                    .amount(it.amount())
                    .unit(it.unit())
                    .buyUrl(it.buyUrl())
                    .build();
            })
            .toList();
    }

    public static List<RecipeStep> toStepsForCreate(final List<CreateStepCommand> commands) {
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

    // 수정용 매핑 메서드들 추가
    public static List<Ingredient> toIngredientsForUpdate(
        final List<UpdateIngredientCommand> commands
    ) {
        return commands.stream()
            .map(it -> {
                validateAmount(it.amount(), it.name());
                return Ingredient.builder()
                    .name(it.name())
                    .amount(it.amount())
                    .unit(it.unit())
                    .buyUrl(it.buyUrl())
                    .build();
            })
            .toList();
    }

    public static List<RecipeStep> toStepsForUpdate(final List<UpdateStepCommand> commands) {
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

    public static RecipeSummaryResponse toResponse(final RecipeSummary recipe) {
        return RecipeSummaryResponse.builder()
            .id(recipe.id())
            .category(recipe.category())
            .title(recipe.title())
            .thumbnailUrl(recipe.thumbnailUrl())
            .createdAt(recipe.createdAt())
            .updatedAt(recipe.updatedAt())
            .build();
    }

    /**
     * BigDecimal amount 값 검증
     */
    private static void validateAmount(BigDecimal amount, String ingredientName) {
        if (amount == null) {
            throw new IllegalArgumentException(
                String.format("재료 '%s'의 양은 필수입니다.", ingredientName));
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                String.format("재료 '%s'의 양은 0보다 커야 합니다. 입력값: %s", ingredientName, amount));
        }

        // 소수점 이하 3자리까지만 허용 (예: 1.234)
        if (amount.scale() > 3) {
            throw new IllegalArgumentException(
                String.format("재료 '%s'의 양은 소수점 이하 3자리까지만 입력 가능합니다. 입력값: %s", ingredientName,
                    amount));
        }

        // 최대값 제한 (예: 999,999.999)
        if (amount.compareTo(new BigDecimal("999999.999")) > 0) {
            throw new IllegalArgumentException(
                String.format("재료 '%s'의 양이 너무 큽니다. 최대 999,999.999까지 입력 가능합니다. 입력값: %s",
                    ingredientName, amount));
        }
    }
}
