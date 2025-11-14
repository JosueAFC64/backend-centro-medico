package cm.apidisponibilidad.dto;

import cm.apidisponibilidad.client.empleado.EmpleadoClientResponse;
import cm.apidisponibilidad.client.especialidad.EspecialidadResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

public record DisponibilidadResponse(
        @Schema(description = "Identificador único de la disponibilidad", example = "1")
        Long id,

        @Schema(description = "Identificador único y nombre completo del médico",
                example = "id: 1, nombreCompleto: 'Hans Luján'")
        EmpleadoClientResponse medico,

        @Schema(description = "Nombre de la especialidad", example = "Cardiología")
        EspecialidadResponse especialidad,

        @Schema(description = "Fecha de la disponibilidad", example = "2025-10-15")
        LocalDate fecha,

        @Schema(description = "Hora de inicio a la que está disponible el médico", example = "10:00:00")
        LocalTime hora_inicio,

        @Schema(description = "Hora de fin a la que está disponible el médico", example = "14:00:00")
        LocalTime hora_fin
) {
}
