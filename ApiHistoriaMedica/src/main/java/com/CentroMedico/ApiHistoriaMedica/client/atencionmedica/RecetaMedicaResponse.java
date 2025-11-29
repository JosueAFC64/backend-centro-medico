package com.CentroMedico.ApiHistoriaMedica.client.atencionmedica;

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

    public record DetalleRecetaResponse(
            @Schema(description = "Identificador único del DetalleReceta", example = "1")
            Long id,

            @Schema(description = "Datos del Medicamento", example = "{}")
            MedicamentosResponse medicamento,

            @Schema(description = "Dosis del Medicamento", example = "500 mg")
            String dosis,

            @Schema(description = "Frecuencia de tomar el Medicamento", example = "Cada 8 horas")
            String frecuencia,

            @Schema(description = "Vía de administración del Medicamento", example = "Oral")
            String viaAdministracion,

            @Schema(description = "Cantidad a comprar del Medicamento", example = "21")
            Integer cantidad
    ) {
    }

    public record MedicamentosResponse(
            @Schema(description = "Identificador único del Medicamento", example = "1")
            Long id,

            @Schema(description = "Nombre del Medicamento", example = "Amoxicilina")
            String nombre,

            @Schema(description = "Presentación del Medicamento", example = "Cápsulas de 500 mg")
            String presentacion
    ) {
    }

}
