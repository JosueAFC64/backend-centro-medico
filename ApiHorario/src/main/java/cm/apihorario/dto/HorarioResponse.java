package cm.apihorario.dto;

import cm.apihorario.client.citamedica.CitaMedicaFeignResponse;
import cm.apihorario.client.consultorio.ConsultorioResponse;
import cm.apihorario.client.empleado.EmpleadoClientResponse;
import cm.apihorario.client.especialidad.EspecialidadResponse;
import cm.apihorario.repository.DetalleHorario;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
public record HorarioResponse(

        @Schema(description = "Identificador único del horario", example = "1")
        Long id,

        @Schema(description = "ID y nombre del empleado",
                example = "id: 1, nombreCompleto: 'Hans Luján'")
        EmpleadoClientResponse empleado,

        @Schema(description = "ID, nombre y costo de la especialidad",
                example = "id: 1, nombre: 'Cardiología', costo: 45.50")
        EspecialidadResponse especialidad,

        @Schema(description = "nro_consultorio y ubicación del consultorio",
                example = "nro_consultorio: 'B0305', ubicacion: 'Primer Piso'")
        ConsultorioResponse consultorio,

        @Schema(description = "Fecha del horario", example = "2025-10-22")
        LocalDate fecha,

        @Schema(description = "Hora de inicio del horario", example = "10:00:00")
        LocalTime horaInicio,

        @Schema(description = "Hora de fin del horario", example = "14:00:00")
        LocalTime horaFin,

        @Schema(description = "Lista de slots del horario", example = "[]")
        List<DetalleHorarioResponse> detalles,

        @Schema(description = "Total de slots del horario", example = "8")
        long totalSlots,

        @Schema(description = "Slots disponibles del horario", example = "5")
        long slotsDisponibles,

        @Schema(description = "Slots ocupados del horario", example = "2")
        long slotsOcupados,

        @Schema(description = "Slots bloqueados del horario", example = "1")
        long slotsBloqueados,

        @Schema(description = "Valida si el horario ya no tiene slots disponibles"
                , example = "true")
        boolean estaCompleto
) {
    
    @Builder
    public record DetalleHorarioResponse(

            @Schema(description = "Identificador único del slot del horario", example = "1")
            Long id,

            @Schema(description = "Hora de inicio del slot del horario", example = "10:00:00")
            LocalTime horaInicio,

            @Schema(description = "Hora de fin del slot del horario", example = "10:30:00")
            LocalTime horaFin,

            @Schema(description = "Estado del slot", example = "DISPONIBLE")
            DetalleHorario.EstadoDetalleHorario estado,

            @Schema(description = "Datos de la cita asociada al slot", example = "[]")
            CitaMedicaFeignResponse cita,

            @Schema(description = "Valida si el slot está disponible o no", example = "false")
            boolean estaDisponible
    ) {
    }
}

