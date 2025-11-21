package cm.apiatencionmedica.client.citamedica;

import cm.apiatencionmedica.exceptions.ServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CitaMedicaFallBackFactory implements FallbackFactory<CitaMedicaFeignClient> {

    private static final String SERVICIO_CAIDO_MSG = "Servicio Cita Medica caído";
    private static final String NOT_FOUND_MSG = "Cita Médica no encontrada";

    // Implementación alternativa de CitaMedicaFeignClient en caso de error
    @Override
    public CitaMedicaFeignClient create(Throwable cause) {
        return new CitaMedicaFeignClient() {
            @Override
            public CitaMedicaFeignResponse obtenerCita(Long id) {
                return handleObtenerCita(cause, id);
            }

            @Override
            public void completarCita(Long id) {
                handleOperacionCita(cause, id);
            }
        };
    }

    private CitaMedicaFeignResponse handleObtenerCita(Throwable cause, Long id) {
        // Si el error es porque no se encontró la Cita Médica (404 NotFound)
        if (cause instanceof FeignException.NotFound) {
            log.warn("Cita Médica con ID: {} no encontrado", id);
            throw new EntityNotFoundException(NOT_FOUND_MSG);
        }

        // Si el error es porque el microservicio de Cita Médica está caído
        if (esServicioCaido(cause)) {
            log.error("Servicio de Citas Médicas caído. Causa: {}", cause.getMessage());
            throw new ServiceUnavailableException(SERVICIO_CAIDO_MSG);
        }

        // Si el error es cualquier otro no manejado
        log.error("Error no manejado en CitaMedicaFeignClient: {}", cause.getMessage());
        throw new RuntimeException("Error al obtener la Cita Médica: " + cause.getMessage(), cause);
    }

    private void handleOperacionCita(Throwable cause, Long id) {

        // Si el error es porque no se encontró la Cita Médica (404 NotFound)
        if (cause instanceof FeignException.NotFound) {
            log.warn("Cita Médica con ID: {} no encontrada para operacion: completar", id);
            throw new EntityNotFoundException(NOT_FOUND_MSG);
        }

        // Si el error es porque el microservicio de Cita Médica está caído
        if (esServicioCaido(cause)) {
            log.error("Servicio de Citas Médicas caído durante operación: completar. Causa: {}", cause.getMessage());
            throw new ServiceUnavailableException(SERVICIO_CAIDO_MSG);
        }

        // Si el error es cualquier otro no manejado
        log.error("Error no manejado en CitaMedicaFeignClient.completar: {}", cause.getMessage());
        throw new RuntimeException("Error al completar la Cita Médica: " + cause.getMessage(), cause);
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