package io.coffeedia.application.usecase.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.coffeedia.domain.model.Ingredient;
import io.coffeedia.domain.model.RecipeStep;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.CategoryType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record RecipeResponse(
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime updatedAt
) {

}
