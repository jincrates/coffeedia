package io.coffeedia.application.usecase.dto;

import io.coffeedia.domain.vo.AccessType;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.BlendType;
import io.coffeedia.domain.vo.Origin;
import io.coffeedia.domain.vo.ProcessType;
import io.coffeedia.domain.vo.RoastLevel;
import java.time.LocalDate;
import lombok.With;

public record UpdateBeanCommand(
    @With
    Long id,
    String name,
    Origin origin,
    String roaster,
    LocalDate roastDate,
    Integer grams,
    RoastLevel roastLevel,
    ProcessType processType,
    BlendType blendType,
    Boolean isDecaf,
    String memo,
    ActiveStatus status,
    AccessType accessType
) {

}
