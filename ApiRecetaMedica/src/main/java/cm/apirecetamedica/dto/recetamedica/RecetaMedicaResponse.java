package cm.apirecetamedica.dto.recetamedica;

import cm.apirecetamedica.client.empleado.EmpleadoClientResponse;
import cm.apirecetamedica.client.paciente.PacienteSimpleResponse;
import cm.apirecetamedica.dto.detallereceta.DetalleRecetaResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record RecetaMedicaResponse(
        @Schema(description = "Identificador único de la Receta Médica", example = "1")
        Long id,

        @Schema(description = "La fecha de solicitud de la Receta Médica", example = "2025-11-26")
        LocalDate fechaSolicitud,

        @Schema(description = "Datos del Paciente", example = "{}")
        PacienteSimpleResponse paciente,

        @Schema(description = "Datos del Médico solicitante", example = "{}")
        EmpleadoClientResponse medico,

        @Schema(description = "Lista de DetalleReceta", example = "[]")
        List<DetalleRecetaResponse> detalles
) {
}
