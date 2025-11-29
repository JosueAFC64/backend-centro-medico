package com.CentroMedico.ApiHistoriaMedica.client.atencionmedica;

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
    public record PacienteSimpleResponse(
            Long idPaciente,
            String nombres,
            String apellidos,
            String dni,
            LocalDate fechaNacimiento
    ) {
    }

    public record EmpleadoClientResponse(
            @Schema(description = "Identificador único del empleado", example = "1")
            Long id,

            @Schema(description = "Nombre completo del empleado", example = "Hans Luján")
            String nombreCompleto
    ) {
    }

    public record DetalleAnalisisResponse(
            @Schema(description = "Identificador único del Detalle Análisis", example = "1")
            Long id,

            @Schema(description = "Tipo de Análisis escogido", example = "{}")
            TipoAnalisisResponse tipoAnalisis
    ) {
    }

    public record TipoAnalisisResponse(
            @Schema(description = "Identificador único del Tipo de Análisis", example = "1")
            Long id,

            @Schema(description = "Nombre del Tipo de Análisis", example = "Radiografía")
            String nombre,

            @Schema(description = "La muestra requerida para el Tipo de Análisis", example = "Ninguna")
            String muestraRequerida
    ) {
    }

}
