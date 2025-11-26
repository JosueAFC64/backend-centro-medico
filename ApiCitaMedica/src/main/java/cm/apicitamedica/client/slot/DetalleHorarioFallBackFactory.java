package cm.apicitamedica.client.slot;

import cm.apicitamedica.exceptions.ServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Component
public class DetalleHorarioFallBackFactory implements FallbackFactory<DetalleHorarioFeignClient> {

    private static final String SERVICIO_CAIDO_MSG = "Servicio Horarios caído";
    private static final String NOT_FOUND_MSG = "Horario o Slot no encontrado";

    // Implementación alternativa de DetalleHorarioFeignClient en caso de error
    @Override
    public DetalleHorarioFeignClient create(Throwable cause) {
        return new DetalleHorarioFeignClient() {
            @Override
            public SlotClientResponse obtenerSlot(Long idHorario, Long idDetalle) {
                return handleOcuparSlotError(cause, idHorario, idDetalle);
            }

            @Override
            public void ocuparSlot(Long idHorario, Long idDetalle, Long idCita) {
                handleOperacionSlotError(cause, idHorario, idDetalle, "ocupar");
            }

            @Override
            public void liberarSlot(Long idHorario, Long idDetalle) {
                handleOperacionSlotError(cause, idHorario, idDetalle, "liberar");
            }

            @Override
            public SlotDisponibleResponse buscarSlotDisponible(Long idMedico, LocalDate fecha, LocalTime hora) {
                throw new RuntimeException("CACA");
            }
        };
    }

    private SlotClientResponse handleOcuparSlotError(Throwable cause, Long idHorario, Long idDetalle) {

        // Si el error es porque no se encontró el recurso (404 NotFound)
        if (cause instanceof FeignException.NotFound) {
            log.warn("Horario con ID: {} o Slot con ID: {} no encontrado", idHorario, idDetalle);
            throw new EntityNotFoundException(NOT_FOUND_MSG);
        }

        // Si el error es porque el microservicio de Horarios está caído
        if (esServicioCaido(cause)) {
            log.error("Servicio de Horarios caído, Causa: {}", cause.getMessage());
            throw new ServiceUnavailableException(SERVICIO_CAIDO_MSG);
        }

        // Si el error es cualquier otro no manejado
        log.error("Error no manejado en DetalleHorarioFeignClient: {}", cause.getMessage());
        throw new RuntimeException("Error al obtener Horario o Slot: " + cause.getMessage(), cause);
    }

    private void handleOperacionSlotError(Throwable cause, Long idHorario, Long idDetalle, String operacion) {

        // Si el error es porque no se encontró el recurso (404 NotFound)
        if (cause instanceof FeignException.NotFound) {
            log.warn(
                    "Horario con ID: {} o Slot con ID: {} no encontrado para operación: {}",
                    idHorario, idDetalle, operacion);
            throw new EntityNotFoundException(NOT_FOUND_MSG);
        }

        // Si el error es porque el microservicio de Horarios está caído
        if (esServicioCaido(cause)) {
            log.error(
                    "Servicio de Horarios caído durante operación: {}, Causa: {}",
                    operacion, cause.getMessage());
            throw new ServiceUnavailableException(SERVICIO_CAIDO_MSG);
        }

        // Si el error es cualquier otro no manejado
        log.error("Error no manejado en DetalleHorarioFeignClient.{}: {}", operacion, cause.getMessage());
        throw new RuntimeException("Error al " + operacion + " slot: " + cause.getMessage(), cause);
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
