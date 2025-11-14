package cm.apiempleado.client.especialidad;

import io.swagger.v3.oas.annotations.media.Schema;

public record EspecialidadResponse(
        @Schema(description = "Identificador único de la especialidad", example = "1")
        Long id,
        @Schema(description = "Nombre de la especialidad", example = "Cardiología")
        String nombre
) {
}
