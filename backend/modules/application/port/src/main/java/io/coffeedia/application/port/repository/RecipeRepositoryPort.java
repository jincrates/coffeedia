package io.coffeedia.application.port.repository;

import io.coffeedia.domain.model.Recipe;

public interface RecipeRepositoryPort {

    Recipe save(Recipe recipe);
}
