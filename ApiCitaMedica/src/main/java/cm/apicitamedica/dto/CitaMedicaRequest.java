package cm.apicitamedica.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record CitaMedicaRequest(
        @Schema(description = "DNI único del paciente", example = "70884572")
        @NotBlank(message = "El DNI es requerido")
        @Size(min = 8, max = 8,message = "El DNI debe tener 8 dígitos")
        String dniPaciente,

        @Schema(description = "Identificador único del horario", example = "1")
        @NotNull(message = "El ID es requerido")
        @Positive(message = "El ID debe ser positivo")
        Long idHorario,

        @Schema(description = "Identificador único del slot", example = "1")
        @NotNull(message = "El ID es requerido")
        @Positive(message = "El ID debe ser positivo")
        Long idDetalleHorario,

        @Schema(description = "Método de Pago de la Cita Médica", example = "EFECTIVO")
        @NotBlank(message = "El método de pago es requerido")
        String metodoPago
) {
}
