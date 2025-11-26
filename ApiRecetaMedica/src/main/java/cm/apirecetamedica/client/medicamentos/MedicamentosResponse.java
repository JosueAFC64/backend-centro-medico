package cm.apirecetamedica.client.medicamentos;

import io.swagger.v3.oas.annotations.media.Schema;

public record MedicamentosResponse(
        @Schema(description = "Identificador único del Medicamento", example = "1")
        Long id,

        @Schema(description = "Nombre del Medicamento", example = "Amoxicilina")
        String nombre,

        @Schema(description = "Presentación del Medicamento", example = "Cápsulas de 500 mg")
        String presentacion
) {
}
