package cm.apipago.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("API de Gestión de Pago de Citas")
                                .version("1.0")
                                .description(
                                        "API para administrar pagos de citas del sistema de gestión de centro médico"
                                )
                );
    }

}
