package cm.apiusuarios.dto;

import cm.apiusuarios.repository.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @Schema(description = "Nombre de usuario del Empleado", example = "Gerald Pacheco")
        @NotBlank(message = "El nombre de usuario es requerido")
        String nombreUsuario,

        @Schema(description = "Email del usuario", example = "gerald@gmail.com")
        @NotBlank(message = "El email del usuario es requerido")
        @Email(message = "El formato del email es inválido")
        String email,

        @Schema(description = "Contraseña del usuario (DNI del empleado)", example = "12345678")
        @NotBlank(message = "La contraseña del usuario es requerida")
        @Size(min = 8, max = 8, message = "La contraseña debe tener 8 dígitos")
        String password,

        @Schema(description = "Rol del usuario/empleado", example = "MEDICO/ENFERMERA/PERSONAL_ADMINISTRATIVO")
        @NotNull(message = "El rol del usuario es requerido")
        User.Roles rol
) {
}
