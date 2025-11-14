package cm.apipago.client.paciente;

public record PacienteClientResponse (
        String nombreCompleto,
        String dni,
        String direccion
) {
}
