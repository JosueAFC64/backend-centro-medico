package cm.apicitamedica.client.slot;

import java.time.LocalDate;
import java.time.LocalTime;

public record SlotDisponibleResponse(
        Long idHorario,
        Long idSlot,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin
) {
}

