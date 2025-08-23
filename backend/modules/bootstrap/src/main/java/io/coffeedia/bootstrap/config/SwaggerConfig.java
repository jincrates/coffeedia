package io.coffeedia.bootstrap.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!prod")
@Configuration
@OpenAPIDefinition(servers = {@Server(url = "/", description = "API Server")})
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(info());
    }

    private Info info() {
        Contact contact = new Contact()
            .name("Coffeedia Teams")
            .email("94jingyu@gmail.com")
            .url("https://coffeedia.io");

        return new Info()
            .title("coffeedia-api")
            .description("☕ Coffeedia - 커피 정보 공유 플랫폼 API")
            .contact(contact)
            .version("1.0");
    }
}
