package io.coffeedia.bootstrap.gateway.auth;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AuthController {

    @GetMapping("auth-user")
    public Mono<AuthUser> getUser(
        @AuthenticationPrincipal OidcUser oidcUser
    ) {
        AuthUser user = AuthUser.builder()
            .username(oidcUser.getPreferredUsername())
            .firstName(oidcUser.getGivenName())
            .lastName(oidcUser.getFamilyName())
            .roles(List.of("employee", "customer"))
            .build();

        return Mono.just(user);
    }
}
