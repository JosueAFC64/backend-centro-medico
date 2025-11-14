package cm.apiespecialidad.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record EspecialidadRequest(
        @Schema(description = "Nombre de la especialidad", example = "Cardiología")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @Schema(description = "Costo Fijo para Cita Médica", example = "45.50")
        @NotNull(message = "El Costo es requerido")
        BigDecimal costo
) {
}
