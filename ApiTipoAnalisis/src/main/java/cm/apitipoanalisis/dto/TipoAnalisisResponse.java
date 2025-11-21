package cm.apitipoanalisis.dto;

import io.swagger.v3.oas.annotations.Parameter;

import java.math.BigDecimal;

public record TipoAnalisisResponse(
        @Parameter(description = "Identificador único del Tipo de Análisis", example = "1")
        Long id,

        @Parameter(description = "Nombre del Tipo de Análisis", example = "Radiografía")
        String nombre,

        @Parameter(description = "Precio del Tipo de Análisis", example = "200.00")
        BigDecimal precio,

        @Parameter(description = "La muestra requerida para el Tipo de Análisis", example = "Ninguna")
        String muestraRequerida
) {
}
