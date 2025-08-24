package io.coffeedia.infrastructure.persistence.jpa.mapper;

import io.coffeedia.domain.model.Bean;
import io.coffeedia.infrastructure.persistence.jpa.entity.BeanJpaEntity;
import io.coffeedia.infrastructure.persistence.jpa.entity.FlavorJpaEntity;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanJpaMapper {

    public static List<BeanJpaEntity> toEntity(final List<Bean> beans) {
        return beans.stream()
            .map(BeanJpaMapper::toEntity)
            .toList();
    }

    public static BeanJpaEntity toEntity(final Bean bean) {
        BeanJpaEntity entity = BeanJpaEntity.builder()
            .id(bean.id())
            .name(bean.name())
            .origin(bean.origin())
            .roaster(bean.roaster())
            .roastDate(bean.roastDate())
            .grams(bean.grams())
            .roastLevel(bean.roastLevel())
            .processType(bean.processType())
            .blendType(bean.blendType())
            .isDecaf(bean.isDecaf())
            .memo(bean.memo())
            .status(bean.status())
            .accessType(bean.accessType())
            .build();

        List<FlavorJpaEntity> flavors = FlavorJpaMapper.toEntity(bean.flavors());
        entity.addFlavors(flavors);

        return entity;
    }

    public static List<Bean> toDomain(final List<BeanJpaEntity> beans) {
        return beans.stream()
            .map(BeanJpaMapper::toDomain)
            .toList();
    }

    public static Bean toDomain(final BeanJpaEntity bean) {
        return Bean.builder()
            .id(bean.getId())
            .name(bean.getName())
            .origin(bean.getOrigin())
            .roaster(bean.getRoaster())
            .roastDate(bean.getRoastDate())
            .grams(bean.getGrams())
            .roastLevel(bean.getRoastLevel())
            .processType(bean.getProcessType())
            .blendType(bean.getBlendType())
            .isDecaf(bean.isDecaf())
            .flavors(FlavorJpaMapper.toDomain(bean.getFlavors()))
            .memo(bean.getMemo())
            .status(bean.getStatus())
            .accessType(bean.getAccessType())
            .createdAt(bean.getCreatedAt())
            .updatedAt(bean.getUpdatedAt())
            .build();
    }
}
