package io.coffeedia.application.usecase.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.EquipmentType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record EquipmentResponse(
    Long equipmentId,
    EquipmentType type,
    String name,
    String brand,
    ActiveStatus status,
    String description,
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    LocalDate buyDate,
    String buyUrl,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime updatedAt
) {

}
