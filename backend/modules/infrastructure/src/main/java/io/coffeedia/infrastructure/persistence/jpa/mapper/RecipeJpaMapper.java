package io.coffeedia.infrastructure.persistence.jpa.mapper;

import io.coffeedia.domain.model.Ingredient;
import io.coffeedia.domain.model.Recipe;
import io.coffeedia.domain.model.RecipeStep;
import io.coffeedia.infrastructure.persistence.jpa.entity.IngredientJpaEntity;
import io.coffeedia.infrastructure.persistence.jpa.entity.RecipeJpaEntity;
import io.coffeedia.infrastructure.persistence.jpa.entity.RecipeStepJpaEntity;
import io.coffeedia.infrastructure.persistence.jpa.entity.RecipeTagJpaEntity;
import io.coffeedia.infrastructure.persistence.jpa.entity.TagJpaEntity;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeJpaMapper {

    public static RecipeJpaEntity toEntity(
        final Recipe recipe,
        final List<TagJpaEntity> tagEntities
    ) {
        RecipeJpaEntity entity = RecipeJpaEntity.builder()
            .id(recipe.id())
            .userId(recipe.userId())
            .category(recipe.category())
            .title(recipe.title())
            .thumbnailUrl(recipe.thumbnailUrl())
            .description(recipe.description())
            .serving(recipe.serving())
            .tips(recipe.tips())
            .status(recipe.status())
            .build();

        // 재료 추가
        if (recipe.ingredients() != null && !recipe.ingredients().isEmpty()) {
            var ingredients = toIngredientEntities(recipe.ingredients(), entity);
            entity.getIngredients().addAll(ingredients);
        }

        // 단계 추가
        if (recipe.steps() != null && !recipe.steps().isEmpty()) {
            var steps = toStepEntities(recipe.steps(), entity);
            entity.getSteps().addAll(steps);
        }

        // 태그 추가
        if (tagEntities != null && !tagEntities.isEmpty()) {
            var recipeTagEntities = toTagEntities(tagEntities, entity);
            entity.getRecipeTags().addAll(recipeTagEntities);
        }

        return entity;
    }

    public static List<IngredientJpaEntity> toIngredientEntities(
        final List<Ingredient> ingredients,
        final RecipeJpaEntity recipe
    ) {
        return ingredients.stream()
            .map(ingredient -> IngredientJpaEntity.builder()
                .recipe(recipe)
                .name(ingredient.name())
                .amount(ingredient.amount())
                .unit(ingredient.unit())
                .build())
            .toList();
    }

    public static List<RecipeStepJpaEntity> toStepEntities(
        final List<RecipeStep> steps,
        final RecipeJpaEntity recipe
    ) {
        return steps.stream()
            .map(it -> RecipeStepJpaEntity.builder()
                .id(it.id())
                .recipe(recipe)
                .sortOrder(it.sortOrder())
                .imageUrl(it.imageUrl())
                .description(it.description())
                .build())
            .toList();
    }

    private static List<RecipeTagJpaEntity> toTagEntities(
        final List<TagJpaEntity> tagEntities,
        final RecipeJpaEntity recipe
    ) {
        return tagEntities.stream()
            .map(tag -> RecipeTagJpaEntity.builder()
                .recipe(recipe)
                .tag(tag)
                .build())
            .toList();
    }

    public static Recipe toDomain(final RecipeJpaEntity recipe) {
        return Recipe.builder()
            .id(recipe.getId())
            .userId(recipe.getUserId())
            .category(recipe.getCategory())
            .title(recipe.getTitle())
            .thumbnailUrl(recipe.getThumbnailUrl())
            .description(recipe.getDescription())
            .serving(recipe.getServing())
            .tips(recipe.getTips())
            .status(recipe.getStatus())
            .tags(extractTagNames(recipe.getRecipeTags()))
            .ingredients(toIngredients(recipe.getIngredients()))
            .steps(toSteps(recipe.getSteps()))
            .createdAt(recipe.getCreatedAt())
            .updatedAt(recipe.getUpdatedAt())
            .build();
    }

    public static List<String> extractTagNames(final List<RecipeTagJpaEntity> recipeTags) {
        return recipeTags.stream()
            .map(recipeTag -> recipeTag.getTag().getName())
            .toList();
    }

    private static List<Ingredient> toIngredients(final List<IngredientJpaEntity> ingredients) {
        return ingredients.stream()
            .map(it -> Ingredient.builder()
                .id(it.getId())
                .name(it.getName())
                .amount(it.getAmount())
                .unit(it.getUnit())
                .buyUrl(it.getBuyUrl())
                .build())
            .toList();
    }

    private static List<RecipeStep> toSteps(final List<RecipeStepJpaEntity> steps) {
        return steps.stream()
            .map(it -> RecipeStep.builder()
                .id(it.getId())
                .recipeId(it.getRecipe().getId())
                .sortOrder(it.getSortOrder())
                .imageUrl(it.getImageUrl())
                .description(it.getDescription())
                .build())
            .toList();
    }
}
