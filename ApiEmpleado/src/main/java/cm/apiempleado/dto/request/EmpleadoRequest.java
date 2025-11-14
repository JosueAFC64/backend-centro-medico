package cm.apiempleado.dto.request;

import cm.apiempleado.repository.Empleados;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public record EmpleadoRequest(
        @Schema(description = "Nombre del empleado", example = "Hans Gerald")
        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 100, message = "Los nombres no pueden exceder los 100 caracteres")
        String nombres,

        @Schema(description = "Apellidos del empleado", example = "Luján Carrión")
        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden exceder los 100 caracteres")
        String apellidos,

        @Schema(description = "Cargo del empleado", example = "MEDICO")
        @NotNull(message = "El cargo es obligatorio")
        Empleados.Cargos cargo,

        @Schema(description = "Número de documento de identidad del empleado", example = "70451623")
        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 dígitos")
        String dni,

        @Schema(description = "Número de teléfono del empleado", example = "945632178")
        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "\\d{9}", message = "El teléfono debe tener exactamente 9 dígitos")
        String telefono,

        @Schema(description = "Correo/email del empleado", example = "hanslujan@gmail.com")
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El formato del correo electrónico no es válido")
        @Size(max = 100, message = "El correo no puede exceder los 100 caracteres")
        String correo,

        @Schema(description = "Fecha de ingreso del empleado", example = "2025-10-08")
        @NotNull(message = "La fecha de ingreso es obligatoria")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate fechaIngreso,

        @Schema(description = "Estado del empleado (true = activo, false = inactivo)", example = "true")
        @NotNull(message = "El estado activo es obligatorio")
        Boolean activo,

        @Schema(description = "Lista de IDs de especialidades del empleado (solo cargo MEDICO)",
                example = "[1, 2]")
        List<Long> especialidadesIds
) {
}