package io.coffeedia.application.usecase.service;

import io.coffeedia.application.port.repository.RecipeRepositoryPort;
import io.coffeedia.application.usecase.GetAllRecipeSummariesUseCase;
import io.coffeedia.application.usecase.dto.RecipeSearchQuery;
import io.coffeedia.application.usecase.dto.RecipeSummaryResponse;
import io.coffeedia.application.usecase.mapper.RecipeMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class GetAllRecipeSummariesService implements GetAllRecipeSummariesUseCase {

    private final RecipeRepositoryPort repository;

    @Override
    @Transactional(readOnly = true)
    public List<RecipeSummaryResponse> invoke(final RecipeSearchQuery query) {
        return repository.findAll(query.pageSize(), query.sort()).stream()
            .map(RecipeMapper::toResponse)
            .toList();
    }
}
