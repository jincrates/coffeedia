package io.coffeedia.bootstrap.gateway.auth;

import static org.assertj.core.api.Assertions.assertThat;

import io.coffeedia.bootstrap.gateway.config.SecurityConfig;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Test
    @DisplayName("미인증 사용자가 인증 정보를 요청하면 로그인 페이지로 리다이렉트된다")
    void redirectToLoginPageWhenUnauthenticatedUserRequestsAuthInfo() {
        webClient
            .get()
            .uri("/auth-user")
            .exchange()
            .expectStatus().isFound()
            .expectHeader().exists("Location");
    }

    @Test
    @DisplayName("인증된 사용자가 인증 정보를 요청하면 사용자 정보가 반환된다")
    void returnUserInfoWhenAuthenticatedUserRequestsAuthInfo() {
        var expectedUser = AuthUser.builder()
            .username("username")
            .firstName("firstName")
            .lastName("lastName")
            .roles(List.of("employee", "customer"))
            .build();

        webClient
            .mutateWith(configureMockOidcLogin(expectedUser))
            .get()
            .uri("/auth-user")
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody(AuthUser.class)
            .value(user -> assertThat(user).isEqualTo(expectedUser));
    }

    private SecurityMockServerConfigurers.OidcLoginMutator configureMockOidcLogin(
        AuthUser expectedUser) {
        return SecurityMockServerConfigurers.mockOidcLogin().idToken(builder -> {
            builder.claim(StandardClaimNames.PREFERRED_USERNAME, expectedUser.username());
            builder.claim(StandardClaimNames.GIVEN_NAME, expectedUser.firstName());
            builder.claim(StandardClaimNames.FAMILY_NAME, expectedUser.lastName());
        });
    }
}
