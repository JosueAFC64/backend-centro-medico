package com.CentroMedico.ApiPaciente.dto;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record PacienteSimpleResponse(
        Long idPaciente,
        String nombres,
        String apellidos,
        String dni,
        LocalDate fechaNacimiento
) {

}


