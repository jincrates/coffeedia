package io.coffeedia.infrastructure.persistence.jdbc.entity;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(value = "flavors")
@Builder
public record FlavorJdbcEntity(
    @Id
    Long id,
    String name
) {

}
