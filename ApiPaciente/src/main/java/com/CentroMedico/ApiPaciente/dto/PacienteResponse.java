package com.CentroMedico.ApiPaciente.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PacienteResponse(
        Long idPaciente,
        String nombres,
        String apellidos,
        String dni,
        String telefono,
        String correo,
        LocalDate fechaNacimiento,
        String telefonoEmergencia,
        String contactoEmergencia,
        String direccion
) {

}
