package cm.apipago.dto.comprobantepago;

import cm.apipago.repository.comprobantepago.ComprobantePago;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ComprobantePagoRequest(
        @Schema(description = "ID de la Cita Médica a pagar", example = "1")
        @NotNull(message = "El ID de Cita Médicas e requerido")
        Long idPagoCita,

        @Schema(description = "Tipo de comprobante de pago a generar", example = "FACTURA/BOLETA")
        @NotNull(message = "El tipo de comprobante de pago es requerido")
        ComprobantePago.TipoComprobante tipoComprobante,

        @Schema(description = "DNI único del Paciente asociado al comprobante de pago", example = "12345678")
        @NotBlank(message = "El DNI del Paciente es requerido")
        @Size(min = 8, max = 8, message = "El DNI del Paciente debe tener 8 dígitos")
        String dniPaciente
) {
}
