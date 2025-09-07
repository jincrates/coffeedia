package io.coffeedia.application.usecase.service;

import io.coffeedia.application.port.repository.RecipeRepositoryPort;
import io.coffeedia.application.usecase.GetRecipeUseCase;
import io.coffeedia.application.usecase.dto.RecipeResponse;
import io.coffeedia.application.usecase.mapper.RecipeMapper;
import io.coffeedia.domain.exception.RecipeNotFoundException;
import io.coffeedia.domain.model.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레시피 상세 조회 서비스 구현체
 */
@Service
@RequiredArgsConstructor
class GetRecipeService implements GetRecipeUseCase {

    private final RecipeRepositoryPort repository;

    @Override
    @Transactional(readOnly = true)
    public RecipeResponse invoke(final Long recipeId) {
        validateRecipeId(recipeId);
        
        Recipe recipe = repository.findById(recipeId)
            .orElseThrow(() -> new RecipeNotFoundException("레시피를 찾을 수 없습니다. ID: " + recipeId));
        
        return RecipeMapper.toResponse(recipe);
    }

    private void validateRecipeId(Long recipeId) {
        if (recipeId == null || recipeId <= 0) {
            throw new IllegalArgumentException("올바른 레시피 ID를 입력해주세요.");
        }
    }
}
