package io.coffeedia.application.usecase.service;

import io.coffeedia.application.port.repository.RecipeRepositoryPort;
import io.coffeedia.application.usecase.UpdateRecipeUseCase;
import io.coffeedia.application.usecase.dto.RecipeResponse;
import io.coffeedia.application.usecase.dto.UpdateRecipeCommand;
import io.coffeedia.application.usecase.mapper.RecipeMapper;
import io.coffeedia.domain.exception.AccessDeniedException;
import io.coffeedia.domain.exception.RecipeNotFoundException;
import io.coffeedia.domain.model.Recipe;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레시피 수정 서비스 구현체
 */
@Service
@RequiredArgsConstructor
class UpdateRecipeService implements UpdateRecipeUseCase {

    private final RecipeRepositoryPort repository;

    @Override
    @Transactional
    public RecipeResponse invoke(final UpdateRecipeCommand command) {
        validateCommand(command);

        Recipe existingRecipe = repository.findById(command.recipeId())
            .orElseThrow(
                () -> new RecipeNotFoundException("레시피를 찾을 수 없습니다. ID: " + command.recipeId()));

        validateOwnership(existingRecipe, command.userId());

        Recipe updatedRecipe = updateRecipe(existingRecipe, command);
        Recipe savedRecipe = repository.save(updatedRecipe);

        return RecipeMapper.toResponse(savedRecipe);
    }

    private void validateCommand(UpdateRecipeCommand command) {
        if (command.recipeId() == null || command.recipeId() <= 0) {
            throw new IllegalArgumentException("올바른 레시피 ID를 입력해주세요.");
        }
        if (command.userId() == null || command.userId() <= 0) {
            throw new IllegalArgumentException("올바른 사용자 ID를 입력해주세요.");
        }
    }

    private void validateOwnership(Recipe recipe, Long userId) {
        if (!recipe.userId().equals(userId)) {
            throw new AccessDeniedException("레시피 수정 권한이 없습니다. ID: " + recipe.id());
        }
    }

    private Recipe updateRecipe(Recipe existingRecipe, UpdateRecipeCommand command) {
        return Recipe.builder()
            .id(existingRecipe.id())
            .userId(existingRecipe.userId())
            .category(command.category() != null ? command.category() : existingRecipe.category())
            .title(command.title() != null ? command.title() : existingRecipe.title())
            .thumbnailUrl(command.thumbnailUrl() != null ? command.thumbnailUrl()
                : existingRecipe.thumbnailUrl())
            .description(command.description() != null ? command.description()
                : existingRecipe.description())
            .serving(command.serving() != null ? command.serving() : existingRecipe.serving())
            .tags(command.tags() != null ? command.tags() : existingRecipe.tags())
            .ingredients(command.ingredients() != null ?
                RecipeMapper.toIngredientsForUpdate(command.ingredients())
                : existingRecipe.ingredients())
            .steps(command.steps() != null ?
                RecipeMapper.toStepsForUpdate(command.steps()) : existingRecipe.steps())
            .tips(command.tips() != null ? command.tips() : existingRecipe.tips())
            .status(command.status() != null ? command.status() : existingRecipe.status())
            .createdAt(existingRecipe.createdAt())
            .updatedAt(LocalDateTime.now())
            .build();
    }
}
