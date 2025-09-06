package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.RecipeSearchQuery;
import io.coffeedia.application.usecase.dto.RecipeSummaryResponse;
import java.util.List;

public interface GetAllRecipeSummariesUseCase {

    List<RecipeSummaryResponse> invoke(RecipeSearchQuery query);
}
