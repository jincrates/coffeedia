package io.coffeedia.application.usecase.service;

import io.coffeedia.application.port.repository.RecipeRepositoryPort;
import io.coffeedia.application.usecase.CreateRecipeUseCase;
import io.coffeedia.application.usecase.dto.CreateRecipeCommand;
import io.coffeedia.application.usecase.dto.RecipeResponse;
import io.coffeedia.application.usecase.mapper.RecipeMapper;
import io.coffeedia.domain.event.RecipeEvent.RecipeCreated;
import io.coffeedia.domain.model.Recipe;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class CreateRecipeService implements CreateRecipeUseCase {

    private final RecipeRepositoryPort repository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public RecipeResponse invoke(final CreateRecipeCommand command) {
        Recipe recipe = RecipeMapper.toDomain(command);
        Recipe saved = repository.save(recipe);

        eventPublisher.publishEvent(
            RecipeCreated.builder()
                .recipeId(saved.id())
                .issuedAt(LocalDateTime.now())
                .build()
        );

        return RecipeMapper.toResponse(saved);
    }
}
