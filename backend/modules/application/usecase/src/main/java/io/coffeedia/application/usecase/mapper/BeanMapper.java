package io.coffeedia.application.usecase.mapper;

import io.coffeedia.application.usecase.dto.BeanResponse;
import io.coffeedia.application.usecase.dto.CreateBeanCommand;
import io.coffeedia.application.usecase.dto.FlavorResponse;
import io.coffeedia.domain.model.Bean;
import io.coffeedia.domain.model.Flavor;
import java.util.List;

public class BeanMapper {

    private BeanMapper() {
    }

    public static Bean toDomain(final CreateBeanCommand command, final List<Flavor> flavors) {
        return Bean.builder()
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
            .accessType(command.accessType())
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
            .accessType(bean.accessType())
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
}
