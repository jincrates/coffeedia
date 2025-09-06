package io.coffeedia.domain.model;

import io.coffeedia.domain.vo.CategoryType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record RecipeSummary(
    Long id,
    Long userId,
    CategoryType category,
    String title,
    String thumbnailUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public RecipeSummary {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        if (category == null) {
            throw new IllegalArgumentException("카테고리는 필수입니다.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }
    }
}
