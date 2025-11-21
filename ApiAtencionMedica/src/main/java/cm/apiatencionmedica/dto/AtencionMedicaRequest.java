package cm.apiatencionmedica.dto;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AtencionMedicaRequest(
        @Parameter(description = "Identificador único de la Cita Médica", example = "1")
        @Positive(message = "El ID de la Cita Médica debe ser positivo")
        @NotNull(message = "El ID de la Cita Médica es requerido")
        Long idCita,

        @Parameter(description = "Diagnostico de la Atención Médica", example = "Tienes cáncer Andy")
        @NotBlank(message = "El diagnostico es requerido")
        String diagnostico,

        @Parameter(description = "Tratamiento de la Atención Médica", example = "Separar tu nicho del cementerio")
        @NotBlank(message = "El tratamiento es requerido")
        String tratamiento,

        @Parameter(description = "Observaciones de la Atención Médica", example = "Es etapa 4 y ya hizo metástasis")
        @NotBlank(message = "Las observaciones es requerida")
        String observaciones
) {
}
