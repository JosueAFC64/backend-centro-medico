package cm.apicitamedica.dto;

import cm.apicitamedica.client.paciente.PacienteSimpleResponse;
import cm.apicitamedica.client.slot.SlotClientResponse;
import cm.apicitamedica.repository.CitaMedica;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record CitaMedicaResponse(
        @Schema(description = "Identificador único de la cita", example = "1")
        Long id,

        @Schema(description = "Datos del paciente asociado a la cita", example = "[]")
        PacienteSimpleResponse paciente,

        @Schema(description = "Costo de la cita", example = "45.50")
        BigDecimal costo,

        @Schema(description = "Estado de la cita", example = "PENDIENTE")
        CitaMedica.EstadoCitaMedica estado,

        @Schema(description = "Detalles de la cita", example = "[]")
        DetallesCita detalles
) {
        public record DetallesCita(
                SlotClientResponse.MedicoResponse medico,
                DatosEspecialidad especialidad,
                LocalDate fecha,
                LocalTime hora,
                SlotClientResponse.ConsultorioResponse consultorio,
                String motivoReemplazo
        ) {}

        public record DatosEspecialidad(
                Long id,
                String nombre
        ) {}
}
