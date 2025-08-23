package io.coffeedia.infrastructure.persistence.jdbc.mapper;

import io.coffeedia.domain.model.Flavor;
import io.coffeedia.infrastructure.persistence.jdbc.entity.FlavorJdbcEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FlavorJdbcMapper {

    public static Flavor toDomain(final FlavorJdbcEntity flavor) {
        return Flavor.builder()
            .id(flavor.id())
            .name(flavor.name())
            .build();
    }
}
