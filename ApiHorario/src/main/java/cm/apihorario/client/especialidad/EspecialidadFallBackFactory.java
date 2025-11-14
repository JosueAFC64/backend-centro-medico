package cm.apihorario.client.especialidad;

import cm.apihorario.exceptions.ServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EspecialidadFallBackFactory implements FallbackFactory<EspecialidadFeignClient> {


    private static final String SERVICIO_CAIDO_MSG = "Servicio Especialidades caído";
    private static final String NOT_FOUND_MSG = "Especialidad no encontrada";

    // Implementación alternativa de EspecialidadFeignClient en caso de error
    @Override
    public EspecialidadFeignClient create(Throwable cause) {
        return id -> {

            // Si el error es porque no se encontró la especialidad (404 NotFound)
            if (cause instanceof FeignException.NotFound) {
                log.warn("Especialidad con ID: {} no encontrada", id);
                throw new EntityNotFoundException(NOT_FOUND_MSG);
            }

            // Si el error es porque el microservicio de especialidad está caído
            if (esServicioCaido(cause)) {
                log.error("Servicio de Especialidades caído. Causa: {}", cause.getMessage());
                throw new ServiceUnavailableException(SERVICIO_CAIDO_MSG);
            }

            // Si el error es por cualquier otro no manejado
            log.error("Error no manejado en EspecialidadFeignClient: {}", cause.getMessage());
            throw new RuntimeException("Error al obtener especialidad: " + cause.getMessage(), cause);
        };
    }

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
