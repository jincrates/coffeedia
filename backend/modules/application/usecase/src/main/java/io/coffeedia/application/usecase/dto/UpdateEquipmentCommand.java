package io.coffeedia.application.usecase.dto;

import io.coffeedia.domain.vo.EquipmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import lombok.Builder;
import lombok.With;

@Builder
public record UpdateEquipmentCommand(
    @With
    Long equipmentId,
    @With
    Long userId,
    @NotNull(message = "장비 타입은 필수입니다")
    EquipmentType type,
    @NotBlank(message = "장비 이름은 필수입니다")
    String name,
    @NotBlank(message = "브랜드는 필수입니다")
    String brand,
    String description,
    @PastOrPresent(message = "구매일은 현재 날짜보다 이후일 수 없습니다")
    LocalDate buyDate,
    String buyUrl
) {

}
