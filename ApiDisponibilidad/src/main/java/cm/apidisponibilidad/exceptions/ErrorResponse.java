package cm.apidisponibilidad.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ErrorResponse(
        @Schema(description = "Timestamp del error", example = "2025-10-08T10:30:00Z")
        LocalDateTime timestamp,
        @Schema(description = "Código HTTP del error", example = "404")
        int status,
        @Schema(description = "Tipo del error", example = "NOT_FOUND")
        String error,
        @Schema(description = "Mensaje descriptivo del error",
                example = "Disponibilidad con ID: 1 no encontrado")
        String message,
        @Schema(description = "Ruta donde ocurrió el error", example = "/disponibilidades/1")
        String path
) {
}
