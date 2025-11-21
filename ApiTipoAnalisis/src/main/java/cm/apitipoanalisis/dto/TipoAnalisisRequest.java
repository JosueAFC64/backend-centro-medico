package cm.apitipoanalisis.dto;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TipoAnalisisRequest(
        @Parameter(description = "Nombre del Tipo de Análisis", example = "Radiografía")
        @NotBlank(message = "El nombre es requerido")
        String nombre,

        @Parameter(description = "Precio del Tipo de Análisis", example = "200.00")
        @NotNull(message = "El precio es requerido")
        BigDecimal precio,

        @Parameter(description = "La muestra requerida para el Tipo de Análisis", example = "Ninguna")
        @NotBlank(message = "La muestra es requerida")
        String muestraRequerida
) {
}
