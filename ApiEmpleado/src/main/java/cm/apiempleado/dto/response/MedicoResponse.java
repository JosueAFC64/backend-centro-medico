package cm.apiempleado.dto.response;

public record MedicoResponse(
        Long idMedico,
        String nombres,
        String apellidos
) {
}
