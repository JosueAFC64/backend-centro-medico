package com.CentroMedico.ApiPaciente.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record PacienteRequest(
        @Schema(example = "Gerald")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 30, message = "El nombre no debe exceder los 30 caracteres")
        String nombres,

        @Schema(example = "Pacheco")
        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 30, message = "El apellido no debe exceder los 30 caracteres")
        String apellidos,

        @Schema(example = "70532148")
        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe tener 8 dígitos numéricos")
        String dni,

        @Schema(example = "987456321")
        @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener 9 dígitos numéricos")
        String telefono,

        @Schema(example = "gerald@gmail.com")
        @Email(message = "Formato de correo inválido")
        @Size(max = 50, message = "El correo no debe exceder los 50 caracteres")
        String correo,

        @Schema(example = "2000-05-10")
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        LocalDate fechaNacimiento,

        @Schema(example = "963258741")
        @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono de emergencia debe tener 9 dígitos numéricos")
        String telefonoEmergencia,

        @Schema(example = "Hans Sideral")
        @Size(max = 30, message = "El contacto de emergencia no debe exceder los 30 caracteres")
        String contactoEmergencia,

        @Schema(example = "Av. Fracasolandia")
        @Size(max = 100, message = "La dirección no debe exceder los 100 caracteres")
        String direccion
) {
}
