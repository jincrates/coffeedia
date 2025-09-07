package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.RecipeResponse;

/**
 * 레시피 상세 조회 UseCase
 */
public interface GetRecipeUseCase {

    /**
     * 레시피 ID로 상세 정보를 조회합니다.
     *
     * @param recipeId 조회할 레시피 ID
     * @return 레시피 상세 정보
     * @throws io.coffeedia.domain.exception.RecipeNotFoundException 레시피를 찾을 수 없는 경우
     */
    RecipeResponse invoke(Long recipeId);
}
