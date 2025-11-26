package cm.apirecetamedica.dto.detallereceta;

import cm.apirecetamedica.client.medicamentos.MedicamentosResponse;
import io.swagger.v3.oas.annotations.media.Schema;

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
