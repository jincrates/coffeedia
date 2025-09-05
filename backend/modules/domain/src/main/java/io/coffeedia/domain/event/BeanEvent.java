package io.coffeedia.domain.event;

import java.time.LocalDateTime;
import lombok.Builder;

public interface BeanEvent extends DomainEvent {

    @Builder
    record BeanCreated(
        Long beanId,
        LocalDateTime issuedAt
    ) implements BeanEvent {

    }

    @Builder
    record BeanUpdated(
        Long beanId,
        LocalDateTime issuedAt
    ) implements BeanEvent {

    }

    @Builder
    record BeanDeleted(
        Long beanId,
        LocalDateTime issuedAt
    ) implements BeanEvent {

    }
}
