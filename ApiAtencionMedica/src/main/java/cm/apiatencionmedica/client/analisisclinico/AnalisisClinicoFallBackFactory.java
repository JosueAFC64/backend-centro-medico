package cm.apiatencionmedica.client.analisisclinico;

import cm.apiatencionmedica.exceptions.ServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AnalisisClinicoFallBackFactory implements FallbackFactory<AnalisisClinicoFeignClient> {

    private static final String SERVICIO_CAIDO_MSG = "Servicio de Análisis Clínico caído";

    @Override
    public AnalisisClinicoFeignClient create(Throwable cause) {
        return id -> {

            // Si el error es porque no se encontró en análisis clínico (404 NotFound)
            if (cause instanceof FeignException.NotFound) {
                log.warn("Análisis Clínico {} no encontrada", id);
                return null;
            }

            // Si el error es porque el microservicio de Análisis Clínico está caído
            if (esServicioCaido(cause)){
                log.error("Servicio de Análisis Clínico caído. Causa: {}", cause.getMessage());
                throw new ServiceUnavailableException(SERVICIO_CAIDO_MSG);
            }

            // Si el error es cualquier otro no manejado
            log.error("Error no manejado en AnalisisClinicoFeignClient: {}", cause.getMessage());
            throw new RuntimeException("Error al obtener Análisis Clínico: " + cause.getMessage(), cause);

        };
    }

    // Metodo auxiliar que determina si el microservicio de especialidad está caído
    private boolean esServicioCaido(Throwable cause){

        // Si el error es porque el circuit breaker se activó
        if (cause instanceof CallNotPermittedException) {
            return true;
        }

        // Si es una excepción de Feign, se revisa el código HTTP
        if (cause instanceof FeignException.FeignClientException feignException) {
            int status = feignException.status();

            // 503 = Service Unavailable, 500 = Internal Server Error, -1 = Sin respuesta
            if (status == 503 || status == 500 || status == -1) {
                return true;
            }
        }

        // Si el mensaje de error contiene estos textos se considera servicio caído
        return cause.getMessage() != null &&
                (cause.getMessage().contains("Connection refused") ||
                        cause.getMessage().contains("connect timed out") ||
                        cause.getMessage().contains("Read timed out"));

    }

}
