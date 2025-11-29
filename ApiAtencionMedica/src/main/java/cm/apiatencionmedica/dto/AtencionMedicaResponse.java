package cm.apiatencionmedica.dto;

import cm.apiatencionmedica.client.analisisclinico.AnalisisClinicoResponse;
import cm.apiatencionmedica.client.recetamedica.RecetaMedicaResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

public record AtencionMedicaResponse(
        @Schema(description = "Identificador único de la Atención Médica", example = "1")
        Long id,

        @Schema(description = "Fecha de la Atención Médica", example = "2025-11-26")
        LocalDate fechaAtencion,

        @Schema(description = "Hora de la Atención Médica", example = "10:00:00")
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime horaAtencion,

        @Schema(description = "Diagnostico de la Atención Médica", example = "Tienes cáncer Andy")
        String diagnostico,

        @Schema(description = "Tratamiento de la Atención Médica", example = "Separar tu nicho del cementerio")
        String tratamiento,

        @Schema(description = "Observaciones de la Atención Médica", example = "Es etapa 4 y ya hizo metástasis")
        String observaciones,

        @Schema(description = "Datos de la Receta Médica", example = "{}")
        RecetaMedicaResponse recetaMedica,

        @Schema(description = "Datos del Análisis Clínico", example = "{}")
        AnalisisClinicoResponse analisisClinico
) {
}
