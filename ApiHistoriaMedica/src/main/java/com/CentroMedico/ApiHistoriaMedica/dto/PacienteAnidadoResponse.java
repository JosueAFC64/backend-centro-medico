package com.CentroMedico.ApiHistoriaMedica.dto;
import lombok.Builder;
import java.time.LocalDate;

@Builder
public record PacienteAnidadoResponse(
        Long idPaciente,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento
) {}


