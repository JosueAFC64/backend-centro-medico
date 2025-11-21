package com.CentroMedico.ApiHistoriaMedica.client.atencionmedica;

import com.CentroMedico.ApiHistoriaMedica.exceptions.ServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AtencionMedicaFallBackFactory implements FallbackFactory<AtencionMedicaFeignClient> {

    private static final String SERVICIO_CAIDO_MSG = "Servicio Atención Medica caído";

    // Implementación alternativa de AtencionMedicaFeignClient en caso de error
    @Override
    public AtencionMedicaFeignClient create(Throwable cause) {
        return id -> {

            // Si el error es porque no se encontró la Atención Médica (404 NotFound)
            if (cause instanceof FeignException.NotFound) {
                log.warn("Atención Médica con ID: {} no encontrado", id);
                throw new EntityNotFoundException("Atención Médica con ID: " + id + " no encontrada");
            }

            // Si el error es porque el microservicio de Atención Médica está caído
            if (esServicioCaido(cause)) {
                log.error("Servicio de Atenciones Médicas caído. Causa: {}", cause.getMessage());
                throw new ServiceUnavailableException(SERVICIO_CAIDO_MSG);
            }

            // Si el error es cualquier otro no manejado
            log.error("Error no manejado en AtencionMedicaFeignClient: {}", cause.getMessage());
            throw new RuntimeException("Error al obtener la Atención Médica: " + cause.getMessage(), cause);

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
