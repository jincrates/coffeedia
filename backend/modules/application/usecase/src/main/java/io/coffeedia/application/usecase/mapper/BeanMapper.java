package io.coffeedia.application.usecase.mapper;

import static io.coffeedia.common.util.ValueUtil.defaultIfNull;

import io.coffeedia.application.usecase.dto.BeanResponse;
import io.coffeedia.application.usecase.dto.CreateBeanCommand;
import io.coffeedia.application.usecase.dto.FlavorResponse;
import io.coffeedia.application.usecase.dto.UpdateBeanCommand;
import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.model.Flavor;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanMapper {

    public static Bean toDomain(final CreateBeanCommand command, final List<Flavor> flavors) {
        return Bean.builder()
            .userId(command.userId())
            .name(command.name())
            .origin(command.origin())
            .roaster(command.roaster())
            .roastDate(command.roastDate())
            .grams(command.grams())
            .roastLevel(command.roastLevel())
            .processType(command.processType())
            .blendType(command.blendType())
            .isDecaf(command.isDecaf())
            .flavors(flavors)
            .memo(command.memo())
            .status(command.status())
            .build();
    }

    public static BeanResponse toResponse(final Bean bean) {
        return BeanResponse.builder()
            .beanId(bean.id())
            .name(bean.name())
            .origin(bean.origin())
            .roaster(bean.roaster())
            .roastDate(bean.roastDate())
            .grams(bean.grams())
            .roastLevel(bean.roastLevel())
            .processType(bean.processType())
            .blendType(bean.blendType())
            .isDecaf(bean.isDecaf())
            .flavors(toResponse(bean.flavors()))
            .memo(bean.memo())
            .status(bean.status())
            .createdAt(bean.createdAt())
            .updatedAt(bean.updatedAt())
            .build();
    }

    private static List<FlavorResponse> toResponse(final List<Flavor> flavors) {
        return flavors.stream()
            .map(BeanMapper::toResponse)
            .toList();
    }

    private static FlavorResponse toResponse(final Flavor flavor) {
        return FlavorResponse.builder()
            .id(flavor.id())
            .name(flavor.name())
            .build();
    }

    public static Bean toDomain(final UpdateBeanCommand command, final Bean existing) {
        return Bean.builder()
            .id(existing.id())
            .userId(existing.userId())
            .name(defaultIfNull(command.name(), existing.name()))
            .origin(defaultIfNull(command.origin(), existing.origin()))
            .roaster(defaultIfNull(command.roaster(), existing.roaster()))
            .roastDate(defaultIfNull(command.roastDate(), existing.roastDate()))
            .grams(defaultIfNull(command.grams(), existing.grams()))
            .roastLevel(defaultIfNull(command.roastLevel(), existing.roastLevel()))
            .processType(defaultIfNull(command.processType(), existing.processType()))
            .blendType(defaultIfNull(command.blendType(), existing.blendType()))
            .isDecaf(defaultIfNull(command.isDecaf(), existing.isDecaf()))
            .flavors(existing.flavors())
            .memo(defaultIfNull(command.memo(), existing.memo()))
            .status(defaultIfNull(command.status(), existing.status()))
            .createdAt(existing.createdAt())
            .updatedAt(existing.updatedAt())  // 기존값을 넣어줘도 Auditing으로 최신화됨
            .build();
    }
}
