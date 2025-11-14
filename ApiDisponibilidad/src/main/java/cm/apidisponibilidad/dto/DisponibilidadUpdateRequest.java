package cm.apidisponibilidad.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public record DisponibilidadUpdateRequest(

        @Schema(description = "ID de la especialidad asociada a la disponibilidad", example = "1")
        @NotNull(message = "La especialidad es obligatoria")
        @Positive(message = "El ID debe ser positivo")
        Long idEspecialidad,

        @Schema(description = "Fecha para la disponibilidad", example = "2025-10-15")
        @NotNull(message = "La fecha es obligatoria")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate fecha,

        @Schema(description = "Hora de inicio a la que está disponible el médico", example = "10:00:00")
        @NotNull(message = "La hora de inicio es obligatoria")
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime hora_inicio,

        @Schema(description = "Hora de fin a la que está disponible el médico", example = "14:00:00")
        @NotNull(message = "La hora de fin es obligatoria")
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime hora_fin
) {
}
