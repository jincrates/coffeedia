package io.coffeedia.application.port.repository;

import io.coffeedia.domain.model.Recipe;
import io.coffeedia.domain.model.RecipeSummary;
import io.coffeedia.domain.vo.PageSize;
import io.coffeedia.domain.vo.SortType;
import java.util.List;
import java.util.Optional;

public interface RecipeRepositoryPort {

    Recipe save(Recipe recipe);

    /**
     * 레시피 ID로 상세 정보를 조회합니다.
     *
     * @param id 레시피 ID
     * @return 레시피 정보 (Optional)
     */
    Optional<Recipe> findById(Long id);

    List<RecipeSummary> findAll(PageSize pageSize, List<SortType> sorts);

    void deleteAll();
}
