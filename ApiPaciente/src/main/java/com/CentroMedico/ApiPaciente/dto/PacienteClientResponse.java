package com.CentroMedico.ApiPaciente.dto;

public record PacienteClientResponse(
        String nombreCompleto,
        String dni,
        String direccion
) {
}
