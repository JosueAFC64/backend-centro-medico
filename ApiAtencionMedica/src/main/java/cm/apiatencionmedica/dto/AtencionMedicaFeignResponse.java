package cm.apiatencionmedica.dto;

import cm.apiatencionmedica.client.citamedica.CitaMedicaFeignResponse;
import io.swagger.v3.oas.annotations.Parameter;

public record AtencionMedicaFeignResponse(
        @Parameter(description = "Identificador único de la Atención Médica", example = "1")
        Long id,

        @Parameter(description = "Diagnostico de la Atención Médica", example = "Tienes cáncer Andy")
        String diagnostico,

        @Parameter(description = "Tratamiento de la Atención Médica", example = "Separar tu nicho del cementerio")
        String tratamiento,

        @Parameter(description = "Observaciones de la Atención Médica", example = "Es etapa 4 y ya hizo metástasis")
        String observaciones,

        @Parameter(description = "Datos de la Cita Médica asociada a la Atención Médica", example = "[]")
        CitaMedicaFeignResponse cita
) {
}
