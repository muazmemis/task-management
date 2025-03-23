package dev.muazmemis.finalproject.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(info = @Info(contact = @Contact(name = "Muaz Memis", email = "muazmemis@gmail.com", url = "https://muazmemis.dev"), description = "OpenApi documentation for Credit Application System", title = "OpenApi specification - Muaz", version = "1.0", license = @License(name = "MIT License", url = "https://opensource.org/licenses/MIT"), termsOfService = "Terms of service"), servers = {
        @Server(description = "Local ENV", url = "http://localhost:8080"),
        @Server(description = "AWS EC2", url = "http://13.61.4.29:8080")
}, security = {
        @SecurityRequirement(name = "bearerAuth")
})
@SecurityScheme(name = "bearerAuth", description = "JWT auth description", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {
}
