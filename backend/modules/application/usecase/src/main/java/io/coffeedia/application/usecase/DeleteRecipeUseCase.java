package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.DeleteRecipeCommand;
import io.coffeedia.application.usecase.dto.DeleteRecipeResponse;

/**
 * 레시피 삭제 UseCase
 */
public interface DeleteRecipeUseCase {

    /**
     * 레시피를 삭제합니다.
     *
     * @param command 삭제할 레시피 정보
     * @return 삭제된 레시피 ID
     * @throws io.coffeedia.domain.exception.RecipeNotFoundException 레시피를 찾을 수 없는 경우
     * @throws io.coffeedia.domain.exception.AccessDeniedException 삭제 권한이 없는 경우
     */
    DeleteRecipeResponse invoke(DeleteRecipeCommand command);
}
