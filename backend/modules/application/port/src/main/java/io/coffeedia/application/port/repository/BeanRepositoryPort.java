package io.coffeedia.application.port.repository;

import io.coffeedia.domain.model.Bean;

public interface BeanRepositoryPort {

    Bean create(Bean bean);
}
