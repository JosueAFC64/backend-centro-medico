package cm.apiusuarios.dto;

import cm.apiusuarios.repository.user.User;

public record UserCookieResponse(
        Long id,
        String nombreUsuario,
        String email,
        User.Roles rol
) {
}
