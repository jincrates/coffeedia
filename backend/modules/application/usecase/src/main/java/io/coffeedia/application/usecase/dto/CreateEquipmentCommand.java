package io.coffeedia.application.usecase.dto;

import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.EquipmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record CreateEquipmentCommand(
    @NotNull(message = "장비 타입은 필수입니다.")
    EquipmentType type,
    @NotBlank(message = "장비 이름은 필수입니다.")
    String name,
    @NotBlank(message = "장비 브랜드는 필수입니다.")
    String brand,
    ActiveStatus status,
    String description,
    @PastOrPresent(message = "구매일자는 현재 날짜보다 이후일 수 없습니다.")
    LocalDate purchaseDate,
    @Pattern(regexp = "^https?://.*", message = "유효한 URL 형식이어야 합니다.")
    String purchaseUrl
) {

}
