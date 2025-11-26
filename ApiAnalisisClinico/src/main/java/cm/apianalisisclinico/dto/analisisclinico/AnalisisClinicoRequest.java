package cm.apianalisisclinico.dto.analisisclinico;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record AnalisisClinicoRequest(
        @Schema(description = "ID del Médico solicitante", example = "1")
        @NotNull(message = "El ID del Médico es requerido")
        Long idMedico,

        @Schema(description = "DNI del Paciente", example = "12345678")
        @NotBlank(message = "El DNI es requerido")
        String dniPaciente,

        @Schema(description = "Fecha de solicitud del Análisis Clínico", example = "2025-11-24")
        @NotNull(message = "La fecha de solicitud es requerida")
        LocalDate fechaSolicitud,

        @Schema(description = "Lista de Tipo Analisis", example = "[1, 2, 3]")
        @NotEmpty(message = "La lista de Tipo Analisis es requerida")
        List<Long> tipoAnalisisIds
) {
}
