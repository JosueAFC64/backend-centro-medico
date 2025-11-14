package cm.apicitamedica.client.slot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record SlotClientResponse(
        MedicoResponse medico,
        EspecialidadResponse especialidad,
        LocalDate fecha,
        LocalTime hora,
        ConsultorioResponse consultorio,
        String estado
) {

    public record MedicoResponse(
            Long id,
            String nombreCompleto
    ) {}

    public record EspecialidadResponse(
            Long id,
            String nombre,
            BigDecimal costo
    ) {}

    public record ConsultorioResponse(
            String nro_consultorio,
            String ubicacion
    ) {}
}
