package io.coffeedia.infrastructure.persistence.jpa.mapper;

import io.coffeedia.domain.model.Flavor;
import io.coffeedia.infrastructure.persistence.jpa.entity.FlavorJpaEntity;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FlavorJpaMapper {

    public static List<Flavor> toDomain(final List<FlavorJpaEntity> flavors) {
        return flavors.stream()
            .map(FlavorJpaMapper::toDomain)
            .toList();
    }

    public static Flavor toDomain(final FlavorJpaEntity flavor) {
        return Flavor.builder()
            .id(flavor.getId())
            .name(flavor.getName())
            .build();
    }

    public static List<FlavorJpaEntity> toEntity(final List<Flavor> flavors) {
        return flavors.stream()
            .map(FlavorJpaMapper::toEntity)
            .toList();
    }

    public static FlavorJpaEntity toEntity(final Flavor flavor) {
        return FlavorJpaEntity.builder()
            .id(flavor.id())
            .name(flavor.name())
            .build();
    }
}
