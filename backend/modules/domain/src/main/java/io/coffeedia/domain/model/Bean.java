package io.coffeedia.domain.model;

import io.coffeedia.domain.vo.AccessType;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.BlendType;
import io.coffeedia.domain.vo.Origin;
import io.coffeedia.domain.vo.ProcessType;
import io.coffeedia.domain.vo.RoastLevel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.Builder;

@Builder
public record Bean(
    Long id,
    Long userId,
    String name,
    Origin origin,
    String roaster,
    LocalDate roastDate,
    Integer grams,
    RoastLevel roastLevel,
    ProcessType processType,
    BlendType blendType,
    Boolean isDecaf,
    List<Flavor> flavors,
    String memo,
    ActiveStatus status,
    AccessType accessType,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public Bean {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("원두 이름은 필수입니다.");
        }
        if (origin == null) {
            throw new IllegalArgumentException("원두 원산지는 필수입니다.");
        }
        if (roaster == null || roaster.isBlank()) {
            throw new IllegalArgumentException("원두 로스터는 필수입니다.");
        }
        if (roastDate == null) {
            throw new IllegalArgumentException("원두 로스팅 일자는 필수입니다.");
        }
        if (grams == null || grams < 0) {
            throw new IllegalArgumentException("원두 그램은 0g 이상이어야 합니다.");
        }
        if (roastLevel == null) {
            roastLevel = RoastLevel.UNKNOWN;
        }
        if (processType == null) {
            processType = ProcessType.UNKNOWN;
        }
        if (blendType == null) {
            blendType = BlendType.UNKNOWN;
        }
        if (flavors == null || flavors.isEmpty()) {
            flavors = Collections.emptyList();
        }
        if (status == null) {
            status = ActiveStatus.ACTIVE;
        }
        if (accessType == null) {
            accessType = AccessType.PRIVATE;
        }
    }
}
