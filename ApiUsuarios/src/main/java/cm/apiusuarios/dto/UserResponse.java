package cm.apiusuarios.dto;

import cm.apiusuarios.repository.user.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponse(
        @Schema(description = "Identificador único del usuario", example = "1")
        Long id,

        @Schema(description = "Nombre de usuario del Empleado", example = "Gerald Pacheco")
        String nombreUsuario,

        @Schema(description = "Email del usuario", example = "gerald@gmail.com")
        String email,

        @Schema(description = "Estado del usuario", example = "TRUE/FALSE")
        Boolean estado,

        @Schema(description = "Rol del usuario/empleado", example = "MEDICO/ENFERMERA/PERSONAL_ADMINISTRATIVO")
        User.Roles rol
) {
}
