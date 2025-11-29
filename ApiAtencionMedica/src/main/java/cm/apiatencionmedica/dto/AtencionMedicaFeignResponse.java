package cm.apiatencionmedica.dto;

import cm.apiatencionmedica.client.analisisclinico.AnalisisClinicoResponse;
import cm.apiatencionmedica.client.citamedica.CitaMedicaFeignResponse;
import cm.apiatencionmedica.client.empleado.EmpleadoClientResponse;
import cm.apiatencionmedica.client.recetamedica.RecetaMedicaResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AtencionMedicaFeignResponse(
        @Parameter(description = "Identificador único de la Atención Médica", example = "1")
        Long id,

        @Parameter(description = "Médico ejecutor de la Atención Médica", example = "{}")
        EmpleadoClientResponse medicoEjecutor,

        @Parameter(description = "Fecha de la Atención Médica", example = "2025-11-26")
        LocalDate fechaAtencion,

        @Parameter(description = "Hora de la Atención Médica", example = "10:30:00")
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime horaAtencion,

        @Parameter(description = "Diagnostico de la Atención Médica", example = "Tienes cáncer Andy")
        String diagnostico,

        @Parameter(description = "Tratamiento de la Atención Médica", example = "Separar tu nicho del cementerio")
        String tratamiento,

        @Parameter(description = "Observaciones de la Atención Médica", example = "Es etapa 4 y ya hizo metástasis")
        String observaciones,

        @Parameter(description = "Datos de la Cita Médica asociada a la Atención Médica", example = "[]")
        CitaMedicaFeignResponse cita,

        @Schema(description = "Datos de la Receta Médica", example = "{}")
        RecetaMedicaResponse recetaMedica,

        @Schema(description = "Datos del Análisis Clínico", example = "{}")
        AnalisisClinicoResponse analisisClinico
) {
}
