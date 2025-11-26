package cm.apiatencionmedica.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record AtencionMedicaRequest(
        @Schema(description = "Identificador único de la Cita Médica", example = "1")
        @Positive(message = "El ID de la Cita Médica debe ser positivo")
        @NotNull(message = "El ID de la Cita Médica es requerido")
        Long idCita,

        @Schema(description = "Fecha de la Atención Médica", example = "2025-11-26")
        @NotNull(message = "La fecha es requerida")
        LocalDate fechaAtencion,

        @Schema(description = "Diagnostico de la Atención Médica", example = "Tienes cáncer Andy")
        @NotBlank(message = "El diagnostico es requerido")
        String diagnostico,

        @Schema(description = "Tratamiento de la Atención Médica", example = "Separar tu nicho del cementerio")
        @NotBlank(message = "El tratamiento es requerido")
        String tratamiento,

        @Schema(description = "Observaciones de la Atención Médica", example = "Es etapa 4 y ya hizo metástasis")
        @NotBlank(message = "Las observaciones es requerida")
        String observaciones,

        @Schema(description = "Identificador único del Médico Ejecutor", example = "1")
        @NotNull(message = "El ID del Médico Ejecutor es requerido")
        @Positive(message = "El ID del Médico Ejecutor debe ser positivo")
        Long idMedicoEjecutor
) {
}
