package io.coffeedia.application.usecase.dto;

import io.coffeedia.domain.vo.AccessType;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.BlendType;
import io.coffeedia.domain.vo.Origin;
import io.coffeedia.domain.vo.ProcessType;
import io.coffeedia.domain.vo.RoastLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.List;

public record CreateBeanCommand(
    @NotBlank(message = "원두 이름은 필수입니다.")
    String name,
    @NotNull(message = "원두 원산지는 필수입니다.")
    @Valid
    Origin origin,
    @NotBlank(message = "원두 로스터는 필수입니다.")
    String roaster,
    @NotNull(message = "원두 로스팅 일자는 필수입니다.")
    LocalDate roastDate,
    @PositiveOrZero(message = "원두 그램은 0g 이상이어야 합니다.")
    int grams,
    RoastLevel roastLevel,
    ProcessType processType,
    BlendType blendType,
    boolean isDecaf,
    List<Long> flavorIds,
    String memo,
    ActiveStatus status,
    AccessType accessType
) {

}
