package io.coffeedia.domain.model;

import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.CategoryType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record Recipe(
    Long id,
    Long userId,
    CategoryType category,
    String title,
    String thumbnailUrl,
    String description,
    Integer serving,  // 1인분, 2인분
    List<String> tags,
    List<Ingredient> ingredients,
    List<RecipeStep> steps,
    String tips,
    ActiveStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public Recipe {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        if (category == null) {
            throw new IllegalArgumentException("카테고리는 필수입니다.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }
        if (serving == null || serving <= 0) {
            throw new IllegalArgumentException("1인분 이상이어야 합니다.");
        }
        if (tags != null && tags.size() > 5) {
            throw new IllegalArgumentException("태그는 최대 5개까지만 가능합니다.");
        }
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("레시피 재료는 필수입니다.");
        }
        if (steps == null || steps.isEmpty()) {
            throw new IllegalArgumentException("레시피 단계는 필수입니다.");
        }
        if (status == null) {
            throw new IllegalArgumentException("상태는 필수입니다.");
        }
    }
}
