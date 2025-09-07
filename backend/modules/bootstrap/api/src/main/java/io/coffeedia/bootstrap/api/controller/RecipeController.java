package io.coffeedia.bootstrap.api.controller;

import static io.coffeedia.common.constant.CommonConstant.USER_ID;

import io.coffeedia.application.usecase.CreateRecipeUseCase;
import io.coffeedia.application.usecase.DeleteRecipeUseCase;
import io.coffeedia.application.usecase.GetAllRecipeSummariesUseCase;
import io.coffeedia.application.usecase.GetRecipeUseCase;
import io.coffeedia.application.usecase.UpdateRecipeUseCase;
import io.coffeedia.application.usecase.dto.CreateRecipeCommand;
import io.coffeedia.application.usecase.dto.DeleteRecipeCommand;
import io.coffeedia.application.usecase.dto.DeleteRecipeResponse;
import io.coffeedia.application.usecase.dto.RecipeResponse;
import io.coffeedia.application.usecase.dto.RecipeSearchQuery;
import io.coffeedia.application.usecase.dto.RecipeSummaryResponse;
import io.coffeedia.application.usecase.dto.UpdateRecipeCommand;
import io.coffeedia.bootstrap.api.controller.docs.RecipeControllerDocs;
import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import io.coffeedia.bootstrap.api.controller.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipes")
public class RecipeController extends BaseController implements RecipeControllerDocs {

    private final CreateRecipeUseCase createUseCase;
    private final GetAllRecipeSummariesUseCase getAllUseCase;
    private final GetRecipeUseCase getRecipeUseCase;
    private final UpdateRecipeUseCase updateRecipeUseCase;
    private final DeleteRecipeUseCase deleteRecipeUseCase;

    @Override
    @PostMapping
    public ResponseEntity<BaseResponse<RecipeResponse>> createRecipe(
        @Valid @RequestBody CreateRecipeCommand command
    ) {
        return created(createUseCase.invoke(command.withUserId(USER_ID)));
    }

    @Override
    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<RecipeSummaryResponse>>> getAllRecipeSummaries(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort  // field1:asc,field2:desc
    ) {
        var response = getAllUseCase.invoke(RecipeSearchQuery.of(page, size, sort));
        return ok(PageResponse.of(page, size, response));
    }

    /**
     * 레시피 상세 조회 API
     *
     * @param recipeId 조회할 레시피 ID
     * @return 레시피 상세 정보
     */
    @Override
    @GetMapping("/{recipeId}")
    public ResponseEntity<BaseResponse<RecipeResponse>> getRecipe(
        @PathVariable Long recipeId
    ) {
        RecipeResponse response = getRecipeUseCase.invoke(recipeId);
        return ok(response);
    }

    @Override
    @PutMapping("/{recipeId}")
    public ResponseEntity<BaseResponse<RecipeResponse>> updateRecipe(
        @PathVariable Long recipeId,
        @Valid @RequestBody UpdateRecipeCommand command
    ) {
        UpdateRecipeCommand commandWithIds = command
            .withRecipeId(recipeId)
            .withUserId(USER_ID);
        
        RecipeResponse response = updateRecipeUseCase.invoke(commandWithIds);
        return ok(response);
    }

    @Override
    @DeleteMapping("/{recipeId}")
    public ResponseEntity<BaseResponse<DeleteRecipeResponse>> deleteRecipe(
        @PathVariable Long recipeId
    ) {
        DeleteRecipeCommand command = DeleteRecipeCommand.of(recipeId, USER_ID);
        DeleteRecipeResponse response = deleteRecipeUseCase.invoke(command);
        return ok(response);
    }
}
