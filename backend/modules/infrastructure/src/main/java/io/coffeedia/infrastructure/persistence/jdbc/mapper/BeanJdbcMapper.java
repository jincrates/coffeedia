package io.coffeedia.infrastructure.persistence.jdbc.mapper;

import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.model.Flavor;
import io.coffeedia.infrastructure.persistence.jdbc.entity.BeanFlavorJdbcEntity;
import io.coffeedia.infrastructure.persistence.jdbc.entity.BeanJdbcEntity;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanJdbcMapper {

    public static List<BeanJdbcEntity> toEntity(final List<Bean> beans) {
        return beans.stream()
            .map(BeanJdbcMapper::toEntity)
            .toList();
    }

    public static BeanJdbcEntity toEntity(final Bean bean) {
        Set<BeanFlavorJdbcEntity> beanFlavors = bean.flavors().stream()
            .map(flavor -> BeanFlavorJdbcEntity.builder()
                .beanId(null)   // beanId는 null로 두면 Spring Data JDBC가 자동 설정
                .flavorId(flavor.id())
                .build())
            .collect(Collectors.toCollection(LinkedHashSet::new));

        return BeanJdbcEntity.builder()
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
            .createdAt(bean.createdAt())
            .updatedAt(bean.updatedAt())
            .beanFlavors(beanFlavors)
            .build();
    }

    public static Bean toDomain(
        final BeanJdbcEntity bean,
        final List<Flavor> flavors
    ) {
        return Bean.builder()
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
            .flavors(flavors)
            .memo(bean.memo())
            .status(bean.status())
            .accessType(bean.accessType())
            .createdAt(bean.createdAt())
            .updatedAt(bean.updatedAt())
            .build();
    }
}
