package cm.apihorario.client.especialidad;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record EspecialidadResponse(
        @Schema(description = "Identificador único de la especialidad", example = "1")
        Long id,

        @Schema(description = "Nombre de la especialidad", example = "Cardiología")
        String nombre,

        @Schema(description = "Costo Fija para Cita Médica", example = "45.50")
        BigDecimal costo
) {
}
