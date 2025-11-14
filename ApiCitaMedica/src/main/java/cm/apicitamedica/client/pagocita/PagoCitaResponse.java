package cm.apicitamedica.client.pagocita;

import java.math.BigDecimal;

public record PagoCitaResponse(
        Long id,
        Long idCitaMedica,
        String dniPaciente,
        BigDecimal montoTotal,
        String metodoPago,
        String estado
) {
}
