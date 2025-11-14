package cm.apipago.dto.pagocita;

import cm.apipago.repository.pagocita.PagoCita;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PagoCitaRequest(
        @Schema(description = "Identificador único de la Cita Médica", example = "1")
        @NotNull(message = "El ID de la Cita es requerido")
        @Positive(message = "El ID de la Cita debe ser positivo")
        Long idCitaMedica,

        @Schema(description = "DNI único del Paciente", example = "12345678")
        @NotBlank(message = "El DNI del Paciente es requerido")
        @Size(min = 8, max = 8, message = "El DNI del Paciente debe tener 8 dígitos")
        String dniPaciente,

        @Schema(description = "Total a pagar por la Cita Médica", example = "45.50")
        @NotNull(message = "El monto total de la Cita es requerido")
        @DecimalMin(value = "1", message = "El monto total de la Cita no puede ser menor a 1.00")
        BigDecimal montoTotal,

        @Schema(description = "Método de Pago de la Cita Médica", example = "EFECTIVO")
        PagoCita.MetodoPago metodoPago
) {
}
