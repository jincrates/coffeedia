package io.coffeedia.application.port.repository;

import io.coffeedia.domain.model.Flavor;
import java.util.List;
import java.util.Set;

public interface FlavorRepositoryPort {

    List<Flavor> findAllByIds(Set<Long> ids);
}
