package cm.apipago.client.citamedica;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record CitaMedicaFeignResponse(
        @Schema(description = "Identificador único de la Cita Médica", example = "1")
        Long id,

        @Schema(description = "Fecha de la Cita Médica", example = "2025-11-01")
        LocalDate fecha,

        @Schema(description = "Hora de la Cita Médica", example = "10:30:00")
        LocalTime hora,

        @Schema(description = "Nombre completo o DNI del Paciente", example = "Gerald Pacheco/12345678")
        DatosPaciente paciente,

        @Schema(description = "Costo Fijo de la Cita Médica", example = "45.50")
        BigDecimal costo,

        @Schema(description = "Nombre completo del Médico", example = "Gerald Pacheco")
        String medico,

        @Schema(description = "Nombre de la especialidad", example = "Cardiología")
        String especialidad
) {
        public record DatosPaciente(
                String nombre,
                String dni
        ) {}

}
