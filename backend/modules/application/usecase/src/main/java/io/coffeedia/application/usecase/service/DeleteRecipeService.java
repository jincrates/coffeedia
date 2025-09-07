package io.coffeedia.application.usecase.service;

import io.coffeedia.application.port.repository.RecipeRepositoryPort;
import io.coffeedia.application.usecase.DeleteRecipeUseCase;
import io.coffeedia.application.usecase.dto.DeleteRecipeCommand;
import io.coffeedia.application.usecase.dto.DeleteRecipeResponse;
import io.coffeedia.domain.exception.AccessDeniedException;
import io.coffeedia.domain.exception.RecipeNotFoundException;
import io.coffeedia.domain.model.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레시피 삭제 서비스 구현체
 */
@Service
@RequiredArgsConstructor
class DeleteRecipeService implements DeleteRecipeUseCase {

    private final RecipeRepositoryPort repository;

    @Override
    @Transactional
    public DeleteRecipeResponse invoke(final DeleteRecipeCommand command) {
        validateCommand(command);
        
        Recipe recipe = repository.findById(command.recipeId())
            .orElseThrow(() -> new RecipeNotFoundException("레시피를 찾을 수 없습니다. ID: " + command.recipeId()));
        
        validateOwnership(recipe, command.userId());
        
        repository.deleteById(command.recipeId());
        
        return DeleteRecipeResponse.of(command.recipeId());
    }

    private void validateCommand(DeleteRecipeCommand command) {
        if (command.recipeId() == null || command.recipeId() <= 0) {
            throw new IllegalArgumentException("올바른 레시피 ID를 입력해주세요.");
        }
        if (command.userId() == null || command.userId() <= 0) {
            throw new IllegalArgumentException("올바른 사용자 ID를 입력해주세요.");
        }
    }

    private void validateOwnership(Recipe recipe, Long userId) {
        if (!recipe.userId().equals(userId)) {
            throw new AccessDeniedException("레시피 삭제 권한이 없습니다. ID: " + recipe.id());
        }
    }
}
