package cm.apianalisisclinico.client.tipoanalisis;

import io.swagger.v3.oas.annotations.media.Schema;

public record TipoAnalisisResponse(
        @Schema(description = "Identificador único del Tipo de Análisis", example = "1")
        Long id,

        @Schema(description = "Nombre del Tipo de Análisis", example = "Radiografía")
        String nombre,

        @Schema(description = "La muestra requerida para el Tipo de Análisis", example = "Ninguna")
        String muestraRequerida
) {
}
