package io.coffeedia.bootstrap.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
        final ServerHttpSecurity http,
        final ReactiveClientRegistrationRepository clientRegistrationRepository
    ) {
        return http
            .authorizeExchange(exchange -> exchange
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2Login(Customizer.withDefaults())
            .logout(logout -> logout.logoutSuccessHandler(
                oidcLogoutSuccessHandler(clientRegistrationRepository)
            ))
            .build();
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(
        final ReactiveClientRegistrationRepository clientRegistrationRepository
    ) {
        var oidcLogoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(
            clientRegistrationRepository
        );
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
        return oidcLogoutSuccessHandler;
    }
}
