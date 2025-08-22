package io.coffeedia.domain.event;

import java.time.LocalDateTime;

public interface DomainEvent {

    LocalDateTime issuedAt();
}
