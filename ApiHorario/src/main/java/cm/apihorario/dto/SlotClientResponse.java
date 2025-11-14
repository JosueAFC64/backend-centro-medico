package cm.apihorario.dto;

import cm.apihorario.client.consultorio.ConsultorioResponse;
import cm.apihorario.client.empleado.EmpleadoClientResponse;
import cm.apihorario.client.especialidad.EspecialidadResponse;
import cm.apihorario.repository.DetalleHorario;

import java.time.LocalDate;
import java.time.LocalTime;

public record SlotClientResponse(
        EmpleadoClientResponse medico,
        EspecialidadResponse especialidad,
        LocalDate fecha,
        LocalTime hora,
        ConsultorioResponse consultorio,
        DetalleHorario.EstadoDetalleHorario estado
) {
}
