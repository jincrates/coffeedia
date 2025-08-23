package io.coffeedia.infrastructure.persistence.jdbc.mapper;

import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.model.Flavor;
import io.coffeedia.domain.vo.Origin;
import io.coffeedia.infrastructure.persistence.jdbc.entity.BeanFlavorJdbcEntity;
import io.coffeedia.infrastructure.persistence.jdbc.entity.BeanJdbcEntity;
import io.coffeedia.infrastructure.persistence.jdbc.entity.FlavorJdbcEntity;
import java.util.List;

public class BeanJdbcMapper {

    private BeanJdbcMapper() {
    }

    public static BeanJdbcEntity toEntity(final Bean bean) {
        return BeanJdbcEntity.builder()
            .id(bean.id())
            .name(bean.name())
            .originCountry(bean.origin().country())
            .originRegion(bean.origin().region())
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
            .createdAt(bean.createdAt())
            .updatedAt(bean.updatedAt())
            .build();
    }

    public static Bean toDomain(
        final BeanJdbcEntity bean,
        final List<Flavor> flavors
    ) {
        return Bean.builder()
            .id(bean.id())
            .name(bean.name())
            .origin(new Origin(bean.originCountry(), bean.originRegion()))
            .roaster(bean.roaster())
            .roastDate(bean.roastDate())
            .grams(bean.grams())
            .roastLevel(bean.roastLevel())
            .processType(bean.processType())
            .blendType(bean.blendType())
            .isDecaf(bean.isDecaf())
            .flavors(flavors)
            .memo(bean.memo())
            .status(bean.status())
            .accessType(bean.accessType())
            .createdAt(bean.createdAt())
            .updatedAt(bean.updatedAt())
            .build();
    }

    public static List<BeanFlavorJdbcEntity> toEntity(
        final BeanJdbcEntity bean,
        final List<Flavor> flavors
    ) {
        return flavors.stream()
            .map(it -> toEntity(bean, it))
            .toList();
    }

    public static BeanFlavorJdbcEntity toEntity(
        final BeanJdbcEntity bean,
        final Flavor flavor
    ) {
        return BeanFlavorJdbcEntity.builder()
            .beanId(bean.id())
            .flavorId(flavor.id())
            .build();
    }

    public static List<Flavor> toDomain(final List<FlavorJdbcEntity> flavors) {
        return flavors.stream()
            .map(BeanJdbcMapper::toDomain)
            .toList();
    }

    public static Flavor toDomain(final FlavorJdbcEntity it) {
        return Flavor.builder()
            .id(it.id())
            .name(it.name())
            .build();
    }
}
