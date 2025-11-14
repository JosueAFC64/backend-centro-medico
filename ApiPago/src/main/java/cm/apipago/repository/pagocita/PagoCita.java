package cm.apipago.repository.pagocita;

import cm.apipago.repository.comprobantepago.ComprobantePago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "pago_cita")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoCita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_comprobante_pago")
    private ComprobantePago comprobantePago;

    @Column(name = "id_cita")
    private Long idCitaMedica;

    @Column(name = "dni_paciente", nullable = false)
    private String dniPaciente;

    @Column(name = "monto_total", nullable = false)
    private BigDecimal montoTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago")
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoPago estado = EstadoPago.PENDIENTE;

    public enum MetodoPago {
        EFECTIVO,
        TARJETA_DEBITO,
        TARJETA_CREDITO,
        TRANSFERENCIA
    }

    public enum EstadoPago {
        PENDIENTE,
        PAGADO,
        FALLIDO
    }

}
