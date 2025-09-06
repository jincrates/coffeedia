package io.coffeedia.application.port.repository;

import io.coffeedia.domain.model.Recipe;
import io.coffeedia.domain.model.RecipeSummary;
import io.coffeedia.domain.vo.PageSize;
import io.coffeedia.domain.vo.SortType;
import java.util.List;

public interface RecipeRepositoryPort {

    Recipe save(Recipe recipe);

    List<RecipeSummary> findAll(PageSize pageSize, List<SortType> sorts);
}
