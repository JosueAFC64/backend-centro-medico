package cm.apirecetamedica.client.empleado;

import io.swagger.v3.oas.annotations.media.Schema;

public record EmpleadoClientResponse(
        @Schema(description = "Identificador único del empleado", example = "1")
        Long id,

        @Schema(description = "Nombre completo del empleado", example = "Hans Luján")
        String nombreCompleto
) {
}
