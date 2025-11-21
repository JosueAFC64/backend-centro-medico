package cm.apipago.dto.pagocita;

import cm.apipago.client.citamedica.CitaMedicaFeignResponse;
import cm.apipago.repository.pagocita.PagoCita;

import java.math.BigDecimal;

public record PagoCitaResponse(
        Long id,
        CitaMedicaFeignResponse citaMedica,
        BigDecimal montoTotal,
        PagoCita.MetodoPago metodoPago,
        PagoCita.EstadoPago estado
) {
}
