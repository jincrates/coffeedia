package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.RecipeResponse;
import io.coffeedia.application.usecase.dto.UpdateRecipeCommand;

/**
 * 레시피 수정 UseCase
 */
public interface UpdateRecipeUseCase {

    /**
     * 레시피 정보를 수정합니다.
     *
     * @param command 수정할 레시피 정보
     * @return 수정된 레시피 정보
     * @throws io.coffeedia.domain.exception.RecipeNotFoundException 레시피를 찾을 수 없는 경우
     * @throws io.coffeedia.domain.exception.AccessDeniedException 수정 권한이 없는 경우
     */
    RecipeResponse invoke(UpdateRecipeCommand command);
}
