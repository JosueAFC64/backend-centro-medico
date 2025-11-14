package cm.apiempleado.dto.response;

import cm.apiempleado.client.especialidad.EspecialidadResponse;
import cm.apiempleado.repository.Empleados;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record EmpleadoResponse(
        @Schema(description = "Identificador único del empleado", example = "1")
        Long id,
        @Schema(description = "Nombres del empleado", example = "Hans")
        String nombres,

        @Schema(description = "Apellidos del empleado", example = "Sideral")
        String apellidos,

        @Schema(description = "Cargo del empleado", example = "MEDICO")
        Empleados.Cargos cargo,
        @Schema(description = "Número de documento de identidad del empleado", example = "70456123")
        String dni,
        @Schema(description = "Número de telefono del empleado", example = "945132784")
        String telefono,
        @Schema(description = "correo/email del empleado", example = "hanspacheco@gmail.com")
        String correo,
        @Schema(description = "Fecha de ingreso del empleado", example = "2025-10-08")
        LocalDate fechaIngreso,
        @Schema(description = "Estado del empleado (true = activo, false = inactivo)", example = "true")
        Boolean activo,
        @Schema(description = "Lista de de especialidades del empleado (solo cargo MEDICO)",
                example = "['Cardiología', 'Traumatología']")
        List<EspecialidadResponse> especialidades
) {
}
