package cm.apihorario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record SlotDisponibleResponse(
        @Schema(description = "Identificador único del horario", example = "1")
        Long idHorario,

        @Schema(description = "Identificador único del slot", example = "5")
        Long idSlot,

        @Schema(description = "Fecha del slot", example = "2025-10-22")
        LocalDate fecha,

        @Schema(description = "Hora de inicio del slot", example = "10:00:00")
        LocalTime horaInicio,

        @Schema(description = "Hora de fin del slot", example = "10:30:00")
        LocalTime horaFin
) {
}

