package com.CentroMedico.ApiHistoriaMedica.dto;
import java.time.LocalDate;

import lombok.Builder;

@Builder
public record PacienteSimpleResponse(
        Long idPaciente,
        String nombres,
        String apellidos,
        String dni,
        LocalDate fechaNacimiento
) {
}
