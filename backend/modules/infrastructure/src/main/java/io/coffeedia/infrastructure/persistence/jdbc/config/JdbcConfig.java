package io.coffeedia.infrastructure.persistence.jdbc.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@Configuration
@EnableJdbcAuditing
@EntityScan(basePackages = "io.coffeedia.infrastructure.persistence.jdbc.entity")
@EnableJdbcRepositories(basePackages = "io.coffeedia.infrastructure.persistence.jdbc.repository")
public class JdbcConfig {

}
