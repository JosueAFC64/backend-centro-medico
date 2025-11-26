package cm.apirecetamedica.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("API de Gestión de Recetas Médicas")
                                .version("1.0")
                                .description(
                                        "API para administrar recetas médicas del sistema de gestión de centro médico"
                                )
                );
    }

}
