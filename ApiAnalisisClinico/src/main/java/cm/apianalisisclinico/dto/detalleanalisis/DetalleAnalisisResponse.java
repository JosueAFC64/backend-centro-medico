package cm.apianalisisclinico.dto.detalleanalisis;

import cm.apianalisisclinico.client.tipoanalisis.TipoAnalisisResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record DetalleAnalisisResponse(
        @Schema(description = "Identificador único del Detalle Análisis", example = "1")
        Long id,

        @Schema(description = "Tipo de Análisis escogido", example = "{}")
        TipoAnalisisResponse tipoAnalisis
) {
}
