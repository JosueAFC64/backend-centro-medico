package cm.apipago.dto.comprobantepago;

import cm.apipago.dto.pagocita.PagoCitaResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ComprobantePagoResponse(
        @Schema(description = "Número de Comprobante", example = "F001-00000001")
        String numeroComprobante,

        @Schema(description = "Citas Médica pagada", example = "1")
        PagoCitaResponse pagoCita,

        @Schema(description = "DNI único del Paciente asociado al comprobante", example = "12345678")
        String dniPaciente,

        @Schema(description = "Nombre completo del Paciente asociado al comprobante",
                example = "Hans Gerald Luján Carrión")
        String nombrePaciente,

        @Schema(description = "Dirección del Paciente asociado al comprobante", example = "Av. Fracasolandia")
        String direccionPaciente,

        @Schema(description = "Suma del monto total de Citas Médicas pagadas", example = "100.50")
        BigDecimal subtotal,

        @Schema(description = "IGV cobrado al subtotal", example = "0.18")
        BigDecimal igv,

        @Schema(description = "Suma del subtotal más el IGV", example = "118.59")
        BigDecimal total,

        @Schema(description = "Fecha de emisión del Comprobante de Pago", example = "2025-11-01T10:51:00")
        LocalDateTime fechaEmision
) {
}
