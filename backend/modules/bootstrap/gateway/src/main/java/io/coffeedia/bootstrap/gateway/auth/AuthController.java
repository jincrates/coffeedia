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
        if (oidcUser == null) {
            return Mono.error(new IllegalArgumentException("인증된 사용자 정보가 필요합니다."));
        }

        AuthUser user = AuthUser.builder()
            .username(oidcUser.getPreferredUsername())
            .firstName(oidcUser.getGivenName())
            .lastName(oidcUser.getFamilyName())
            .roles(List.of("employee", "customer"))  // TODO: 임시 하드코딩
            .build();

        return Mono.just(user);
    }
}
