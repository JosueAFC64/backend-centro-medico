package cm.apianalisisclinico.dto.analisisclinico;

import cm.apianalisisclinico.client.empleado.EmpleadoClientResponse;
import cm.apianalisisclinico.client.paciente.PacienteSimpleResponse;
import cm.apianalisisclinico.dto.detalleanalisis.DetalleAnalisisResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record AnalisisClinicoResponse(
        @Schema(description = "Identificador único del Análisis Clínico", example = "1")
        Long id,

        @Schema(description = "Fecha de la solicitud del Análisis Clínico", example = "2025-11-22")
        LocalDate fechaSolicitud,

        @Schema(description = "Datos del Paciente", example = "{}")
        PacienteSimpleResponse paciente,

        @Schema(description = "Datos del Médico solicitante", example = "{}")
        EmpleadoClientResponse medico,

        @Schema(description = "Lista de Detalle Análisis", example = "[]")
        List<DetalleAnalisisResponse> detalles
) {
}
