package cm.apicitamedica.client.paciente;

import java.time.LocalDate;

public record PacienteSimpleResponse(
        Long idPaciente,
        String nombres,
        String apellidos,
        String dni,
        LocalDate fechaNacimiento
) {
}
