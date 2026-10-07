package br.com.startjob.acesso.entrypoint.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Set;

@Configuration
public class OpenApiConfig {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/login",
            "/restful-services/app/login",
            "/restful-services/app/health",
            "/restful-services/access/action",
            "/restful-services/login/action",
            "/restful-services/login/do",
            "/restful-services/login/interno"
    );

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Acesso Web API")
                        .version("0.1.0")
                        .description("API Spring Boot em migração do monólito Jakarta EE. "
                                + "Os caminhos /sistema/restful-services/* preservam o contrato dos clientes existentes."))
                .schemaRequirement("bearer-jwt", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }

    @Bean
    public OpenApiCustomizer publicEndpointSecurityOverride() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }

            PUBLIC_PATHS.forEach(path -> {
                PathItem pathItem = openApi.getPaths().get(path);
                if (pathItem != null) {
                    pathItem.readOperations().forEach(operation -> operation.setSecurity(Collections.emptyList()));
                }
            });
        };
    }
}
