package io.coffeedia.application.port.repository;

import io.coffeedia.domain.model.Flavor;
import java.util.List;

public interface FlavorRepositoryPort {

    List<Flavor> getAllFlavors(List<Long> ids);
}
