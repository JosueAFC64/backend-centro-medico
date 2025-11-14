package cm.apiconsultorio.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ConsultorioResponse(
        @Schema(description = "Número único del consultorio", example = "B0305")
        String nro_consultorio,

        @Schema(description = "Ubicación del consultorio", example = "Primer Piso")
        String ubicacion
) {
}
