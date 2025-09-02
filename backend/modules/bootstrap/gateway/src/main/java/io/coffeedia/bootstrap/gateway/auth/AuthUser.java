package io.coffeedia.bootstrap.gateway.auth;

import java.util.List;
import lombok.Builder;

@Builder
public record AuthUser(
    String username,
    String firstName,
    String lastName,
    List<String> roles
) {

}
