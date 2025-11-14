package cm.apiusuarios.dto;

public record AuthRequest(
        String email,
        String password
) {
}
