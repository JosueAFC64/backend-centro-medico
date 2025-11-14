package cm.apihorario.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public record HorarioRequest(
        @Schema(description = "Identificador único del empleado", example = "1")
        @NotNull(message = "El ID del empleado es obligatorio")
        @Positive(message = "El ID debe ser positivo")
        Long idEmpleado,

        @Schema(description = "Número único del consultorio", example = "B0103")
        @NotBlank(message = "El N° del consultorio es obligatorio")
        String nro_consultorio,

        @Schema(description = "Fecha para el horario", example = "2025-10-22")
        @NotNull(message = "La fecha es obligatoria")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate fecha,

        @Schema(description = "Hora de inicio del horario", example = "10:00:00")
        @NotNull(message = "La hora de inicio es obligatoria")
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime horaInicio,

        @Schema(description = "Hora de fin del horario", example = "14:00:00")
        @NotNull(message = "La hora de fin es obligatoria")
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime horaFin,

        @Schema(description = "Duración en min de cada slot del horario (por defecto es 30)",
                example = "30")
        @Positive(message = "La duración del slot debe ser mayor a 0")
        Integer duracionSlotMinutos,

        @Schema(description = "Identificador único de la especialidad", example = "1")
        @NotNull(message = "El ID de la especialidad es obligatorio")
        @Positive(message = "El ID debe ser positivo")
        Long idEspecialidad
) {
}
