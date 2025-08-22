package io.coffeedia.infrastructure.persistence.jdbc.entity;

import io.coffeedia.domain.vo.AccessType;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.BlendType;
import io.coffeedia.domain.vo.ProcessType;
import io.coffeedia.domain.vo.RoastLevel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

@Table(value = "beans")
@Builder
public record BeanJdbcEntity(
    @Id
    Long id,
    String name,
    String originCountry,
    String originRegion,
    String roaster,
    LocalDate roastDate,
    int grams,
    RoastLevel roastLevel,
    ProcessType processType,
    BlendType blendType,
    boolean isDecaf,
    String memo,
    ActiveStatus status,
    AccessType accessType,
    @CreatedDate
    LocalDateTime createdAt,
    @LastModifiedDate
    LocalDateTime updatedAt
) {

}
