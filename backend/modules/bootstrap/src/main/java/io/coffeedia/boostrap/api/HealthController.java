package io.coffeedia.boostrap.api;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final Environment env;

    @GetMapping("/health")
    public String health() {
        return String.format(
            "It's Working in Coffeedia Backend on LOCAL PORT %s (SERVER PORT %s)",
            env.getProperty("local.server.port"),
            env.getProperty("server.port")
        );
    }
}
