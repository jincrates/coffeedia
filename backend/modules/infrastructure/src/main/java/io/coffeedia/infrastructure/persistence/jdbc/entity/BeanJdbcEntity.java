package io.coffeedia.infrastructure.persistence.jdbc.entity;

import io.coffeedia.domain.vo.AccessType;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.BlendType;
import io.coffeedia.domain.vo.Origin;
import io.coffeedia.domain.vo.ProcessType;
import io.coffeedia.domain.vo.RoastLevel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table(value = "beans")
@Builder
public record BeanJdbcEntity(
    @Id
    Long id,
    String name,
    @Embedded(onEmpty = OnEmpty.USE_NULL, prefix = "origin_")
    Origin origin,
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
    LocalDateTime updatedAt,
    @MappedCollection(idColumn = "bean_id", keyColumn = "bean_id")
    Set<BeanFlavorJdbcEntity> beanFlavors
) {

    public BeanJdbcEntity {
        if (beanFlavors == null) {
            beanFlavors = new HashSet<>();
        }
    }

    public List<Long> flavorIds() {
        return beanFlavors.stream()
            .map(BeanFlavorJdbcEntity::flavorId)
            .toList();
    }
}
