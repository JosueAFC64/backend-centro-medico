package com.CentroMedico.ApiHistoriaMedica.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record HistoriaMedicaRequest(

        @NotBlank(message = "El DNI del paciente es obligatorio")
        @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe tener 8 dígitos numéricos")
        String dniPaciente, // Usado para setear idHistoriaMedica y validar al paciente

        @NotNull(message = "El peso es obligatorio")
        @DecimalMin(value = "0.1", message = "El peso debe ser mayor a cero")
        Double peso,

        @NotNull(message = "La talla es obligatoria")
        @DecimalMin(value = "0.1", message = "La talla debe ser mayor a cero")
        Double talla,

        @NotBlank(message = "El tipo de sangre es obligatorio")
        @Size(max = 10, message = "El tipo de sangre no debe exceder los 10 caracteres")
        String tipoSangre,

        @Size(max = 255, message = "Las alergias no deben exceder los 255 caracteres")
        String alergias,

        @Size(max = 500, message = "Los antecedentes familiares no deben exceder los 500 caracteres")
        String antecedentesFamiliares,

        @Size(max = 500, message = "Los antecedentes personales no deben exceder los 500 caracteres")
        String antecedentesPersonales
) {
}
