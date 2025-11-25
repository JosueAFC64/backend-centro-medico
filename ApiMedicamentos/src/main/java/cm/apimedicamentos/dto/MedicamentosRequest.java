package cm.apimedicamentos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record MedicamentosRequest(
        @Schema(description = "Nombre del Medicamento", example = "Amoxicilina")
        @NotBlank(message = "El nombre es requerido")
        String nombre,

        @Schema(description = "Presentación del Medicamento", example = "Cápsulas de 500 mg")
        @NotBlank(message = "La presentación es requerida")
        String presentacion
) {
}
