package io.coffeedia.bootstrap.api.controller;

import static io.coffeedia.common.constant.CommonConstant.USER_ID;

import io.coffeedia.application.usecase.CreateRecipeUseCase;
import io.coffeedia.application.usecase.GetAllRecipeSummariesUseCase;
import io.coffeedia.application.usecase.dto.CreateRecipeCommand;
import io.coffeedia.application.usecase.dto.RecipeResponse;
import io.coffeedia.application.usecase.dto.RecipeSearchQuery;
import io.coffeedia.application.usecase.dto.RecipeSummaryResponse;
import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import io.coffeedia.bootstrap.api.controller.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipes")
public class RecipeController extends BaseController {

    private final CreateRecipeUseCase createUseCase;
    private final GetAllRecipeSummariesUseCase getAllUseCase;

    @PostMapping
    public ResponseEntity<BaseResponse<RecipeResponse>> createRecipe(
        @Valid @RequestBody CreateRecipeCommand command
    ) {
        return created(createUseCase.invoke(command.withUserId(USER_ID)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<RecipeSummaryResponse>>> getAllRecipeSummaries(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort  // field1:asc,field2:desc
    ) {
        var response = getAllUseCase.invoke(RecipeSearchQuery.of(page, size, sort));
        return ok(PageResponse.of(page, size, response));
    }
}
