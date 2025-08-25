package io.coffeedia.bootstrap.api;

import io.coffeedia.bootstrap.api.docs.HealthControllerDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthController implements HealthControllerDocs {

    private final Environment env;

    @Override
    @GetMapping("/health")
    public String health() {
        return String.format(
            "It's Working in Coffeedia Backend on LOCAL PORT %s (SERVER PORT %s)",
            env.getProperty("local.server.port"),
            env.getProperty("server.port")
        );
    }
}
