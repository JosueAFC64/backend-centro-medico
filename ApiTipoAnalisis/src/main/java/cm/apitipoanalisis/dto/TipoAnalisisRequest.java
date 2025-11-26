package cm.apitipoanalisis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TipoAnalisisRequest(
        @Schema(description = "Nombre del Tipo de Análisis", example = "Radiografía")
        @NotBlank(message = "El nombre es requerido")
        String nombre,

        @Schema(description = "La muestra requerida para el Tipo de Análisis", example = "Ninguna")
        @NotBlank(message = "La muestra es requerida")
        String muestraRequerida
) {
}
