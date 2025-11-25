package cm.apirecetamedica.dto.detallereceta;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DetalleRecetaRequest(
        @Schema(description = "Identificador único del Medicamento", example = "1")
        @NotNull(message = "El ID del Medicamento es requerido")
        @Positive(message = "El ID del Medicamento debe ser positivo")
        Long idMedicamento,

        @Schema(description = "Dosis del Medicamento", example = "500 mg")
        @NotBlank(message = "La dosis es requerida")
        String dosis,

        @Schema(description = "Frecuencia de tomar el Medicamento", example = "Cada 8 horas")
        @NotBlank(message = "La frecuencia es requerida")
        String frecuencia,

        @Schema(description = "Vía de administración del Medicamento", example = "Oral")
        @NotBlank(message = "La vía de administración es requerida")
        String viaAdministracion,

        @Schema(description = "Cantidad a comprar del Medicamento", example = "21")
        @NotNull(message = "La cantidad es requerida")
        @Positive(message = "La cantidad debe ser positiva")
        Integer cantidad
) {
}
