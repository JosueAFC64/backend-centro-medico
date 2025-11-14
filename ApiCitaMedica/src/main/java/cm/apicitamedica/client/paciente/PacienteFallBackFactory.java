package cm.apicitamedica.client.paciente;

import cm.apicitamedica.exceptions.ServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PacienteFallBackFactory implements FallbackFactory<PacienteFeignClient> {

    private static final String SERVICIO_CAIDO_MSG = "Servicio Pacientes caído";

    // Implementación alternativa de PacienteFeignClient en caso de error
    @Override
    public PacienteFeignClient create(Throwable cause) {
        return dni -> {

            // Si el error es porque no se encontró el Paciente (404 NotFound)
            if (cause instanceof FeignException.NotFound) {
                log.warn("Paciente con ID: {} no encontrado", dni);
                return new PacienteSimpleResponse(
                        null,
                        null,
                        null,
                        dni,
                        null);
            }

            // Si el error es porque el microservicio de Pacientes está caído
            if (esServicioCaido(cause)) {
                log.error("Servicio de Pacientes caído. Causa: {}", cause.getMessage());
                throw new ServiceUnavailableException(SERVICIO_CAIDO_MSG);
            }

            // Si el error es cualquier otro no manejado
            log.error("Error no manejado en PacienteFeignClient: {}", cause.getMessage());
            throw new RuntimeException("Error al obtener Paciente: " + cause.getMessage(), cause);
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
