package cm.apiconsultorio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ConsultorioRequest(
        @Schema(description = "Número único del consultorio", example = "B0305")
        @NotBlank(message = "El número de consultorio es obligatorio")
        @Size(min = 5, max = 5, message = "El número de consultorio debe tener 5 cáracteres")
        @Pattern(regexp = "^[A-Z]\\d{4}$",
                message = "El número de consultorio debe comenzar con una letra seguida de 4 dígitos")
        String nro_consultorio,

        @Schema(description = "Ubicación del consultorio", example = "Primer Piso")
        @NotBlank(message = "La ubicación es obligatoria")
        @Pattern(regexp = "^(Primer|Segundo|Tercer) Piso$",
                message = "La ubicación debe ser Primer, Segundo o Tercer Piso")
        String ubicacion
) {
}
