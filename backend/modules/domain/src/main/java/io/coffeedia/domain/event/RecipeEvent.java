package io.coffeedia.domain.event;

import java.time.LocalDateTime;
import lombok.Builder;

public interface RecipeEvent extends DomainEvent {

    @Builder
    record RecipeCreated(
        Long recipeId,
        LocalDateTime issuedAt
    ) implements RecipeEvent {

    }
}
