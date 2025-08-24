package io.coffeedia.infrastructure.persistence.jpa.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EntityScan(basePackages = "io.coffeedia.infrastructure.persistence.jpa.entity")
@EnableJpaRepositories(basePackages = "io.coffeedia.infrastructure.persistence.jpa.repository")
public class JpaConfig {

}
