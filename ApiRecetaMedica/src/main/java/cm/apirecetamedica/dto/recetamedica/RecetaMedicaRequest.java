package cm.apirecetamedica.dto.recetamedica;

import cm.apirecetamedica.dto.detallereceta.DetalleRecetaRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record RecetaMedicaRequest(
        @Schema(description = "ID de la Atención Médica", example = "1")
        @NotNull(message = "El ID de la Atención Médica es requerido")
        Long idAtencion,

        @Schema(description = "ID del Médico solicitante", example = "1")
        @NotNull(message = "El ID del Médico es requerido")
        Long idMedico,

        @Schema(description = "DNI del Paciente", example = "12345678")
        @NotBlank(message = "El DNI del Paciente es requerido")
        String dniPaciente,

        @Schema(description = "La fecha de solicitud de la Receta Médica", example = "2025-11-26")
        @NotNull(message = "La fecha de solicitud es requerida")
        LocalDate fechaSolicitud,

        @Schema(description = "Lista de DetalleReceta", example = "[]")
        @NotEmpty(message = "La lista de DetalleReceta es requerida")
        List<DetalleRecetaRequest> detalles
) {
}
