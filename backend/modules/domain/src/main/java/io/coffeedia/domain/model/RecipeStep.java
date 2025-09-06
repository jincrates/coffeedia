package io.coffeedia.domain.model;

import lombok.Builder;

@Builder
public record RecipeStep(
    Long id,
    Long recipeId,
    Integer sortOrder,  // 레시피 순서
    String imageUrl,    // 첨부 이미지
    String description  // 레시피 설명
) {

}
