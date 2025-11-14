package com.CentroMedico.ApiPaciente.dto;

public record PacienteSumResponse(
        Long idPaciente,
        String nombres,
        String apellidos,
        String dni
) {
}
