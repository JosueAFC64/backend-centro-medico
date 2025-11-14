package cm.apiempleado.dto.response;

import cm.apiempleado.client.especialidad.EspecialidadResponse;
import cm.apiempleado.repository.Empleados;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record EmpleadoSumResponse(
        @Schema(description = "Identificador único del empleado", example = "1")
        Long id,
        @Schema(description = "Nombre completo del empleado", example = "Hans Sideral")
        String nombreCompleto,
        @Schema(description = "Cargo del empleado", example = "MEDICO")
        Empleados.Cargos cargo,
        @Schema(description = "Lista de especialidades del empleado",
                example = "['Cardiología', 'Traumatología']")
        List<EspecialidadResponse> especialidades
) {
}
