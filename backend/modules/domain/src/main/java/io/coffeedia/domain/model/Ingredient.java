package io.coffeedia.domain.model;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record Ingredient(
    Long id,
    Long recipeId,
    String name,     // 재료명 (예: "우유")
    BigDecimal amount,   // 양 (예: 200, 1.5)
    String unit,     // 단위 (예: "ml", "큰술", "개")
    String buyUrl    // 구매 url
) {

}
