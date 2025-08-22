package io.coffeedia.infrastructure.persistence.jdbc.mapper;

import io.coffeedia.domain.model.Flavor;
import io.coffeedia.infrastructure.persistence.jdbc.entity.FlavorJdbcEntity;

public class FlavorJdbcMapper {

    private FlavorJdbcMapper() {
    }

    public static Flavor toDomain(final FlavorJdbcEntity flavor) {
        return Flavor.builder()
            .id(flavor.id())
            .name(flavor.name())
            .build();
    }
}
