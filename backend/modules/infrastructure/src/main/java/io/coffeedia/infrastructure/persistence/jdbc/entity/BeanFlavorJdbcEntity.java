package io.coffeedia.infrastructure.persistence.jdbc.entity;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(value = "bean_flavors")
@Builder
public record BeanFlavorJdbcEntity(
    @Id
    Long id,
    Long beanId,
    Long flavorId
) {

}
