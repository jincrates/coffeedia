package io.coffeedia.application.usecase.dto;

import io.coffeedia.domain.vo.AccessType;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.BlendType;
import io.coffeedia.domain.vo.Origin;
import io.coffeedia.domain.vo.ProcessType;
import io.coffeedia.domain.vo.RoastLevel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record BeanResponse(
    Long beanId,
    String name,
    Origin origin,
    String roaster,
    LocalDate roastDate,
    int grams,
    RoastLevel roastLevel,
    ProcessType processType,
    BlendType blendType,
    boolean isDecaf,
    List<FlavorResponse> flavors,
    String memo,
    ActiveStatus status,
    AccessType accessType,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}
