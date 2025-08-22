package io.coffeedia.application.usecase.mapper;

import io.coffeedia.application.usecase.dto.CreateBeanCommand;
import io.coffeedia.application.usecase.dto.CreateBeanResponse;
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

    public static CreateBeanResponse toResponse(final Bean bean) {
        return CreateBeanResponse.builder()
            .beanId(bean.id())
            .build();
    }
}
