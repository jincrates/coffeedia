package io.coffeedia.application.usecase.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.coffeedia.domain.vo.CategoryType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record RecipeSummaryResponse(
    Long id,
    CategoryType category,
    String title,
    String thumbnailUrl,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime updatedAt
) {

}
