package io.coffeedia.application.usecase;

import io.coffeedia.application.usecase.dto.CreateRecipeCommand;
import io.coffeedia.application.usecase.dto.RecipeResponse;

public interface CreateRecipeUseCase {

    RecipeResponse invoke(CreateRecipeCommand command);
}
