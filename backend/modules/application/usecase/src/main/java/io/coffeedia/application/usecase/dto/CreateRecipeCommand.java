package io.coffeedia.application.usecase.dto;

import io.coffeedia.domain.vo.CategoryType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.With;
import org.hibernate.validator.constraints.Length;

@Builder
public record CreateRecipeCommand(
    @With
    Long userId,
    @NotNull(message = "카테고리는 필수입니다.")
    CategoryType category,
    @NotBlank(message = "제목은 필수입니다.")
    String title,
    String thumbnailUrl,
    @Length(max = 500, message = "상세 설명은 500자까지만 가능합니다.")
    String description,
    @Min(value = 1, message = "1인분 이상이어야 합니다.")
    Integer serving,  // 1인분, 2인분
    @Size(max = 5, message = "태그는 최대 5개까지만 가능합니다.")
    List<String> tags,
    @NotEmpty(message = "레시피 재료는 필수입니다.")
    @Valid
    List<CreateIngredientCommand> ingredients,
    @NotEmpty(message = "레시피 단계는 필수입니다.")
    @Valid
    List<CreateStepCommand> steps,
    @Length(max = 200, message = "레시피 팁은 200자까지만 가능합니다.")
    String tips
) {

    @Builder
    public record CreateIngredientCommand(
        @NotBlank(message = "재료 이름은 필수입니다.")
        String name,     // 재료명 (예: "우유")
        @DecimalMin(value = "0.0")
        BigDecimal amount,   // 양 (예: 200, 1.5)
        @NotBlank(message = "단위는 필수입니다.")
        String unit,     // 단위 (예: "ml", "큰술", "개")
        String buyUrl    // 구매 url
    ) {

    }

    @Builder
    public record CreateStepCommand(
        String imageUrl,    // 첨부 이미지
        @NotBlank(message = "레시피 설명은 필수입니다.")
        String description  // 레시피 설명
    ) {

    }
}
