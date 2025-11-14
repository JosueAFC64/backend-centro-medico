package cm.apipago.repository.comprobantepago;

import cm.apipago.repository.pagocita.PagoCita;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comprobante_pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobantePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "comprobantePago", cascade = CascadeType.ALL)
    private PagoCita pagoCita;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false)
    private TipoComprobante tipoComprobante;

    @Column(name = "serie")
    private String serie;

    @Column(name = "numero_comprobante")
    private String numeroComprobante;

    @Column(name = "numero_completo_comprobante")
    private String numeroCompletoComprobante;

    @Column(name = "dni_paciente", nullable = false)
    private String dniPaciente;

    @Column(name = "nombre_paciente", nullable = false)
    private String nombrePaciente;

    @Column(name = "direccion_paciente", nullable = false)
    private String direccionPaciente;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;

    @Column(name = "igv", nullable = false)
    private BigDecimal igv;

    @Column(name = "total", nullable = false)
    private BigDecimal total;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    public enum TipoComprobante {
        BOLETA,
        FACTURA
    }

    public void addPagoCita(PagoCita pago) {
        this.pagoCita = pago;
        pago.setComprobantePago(this);
    }

}
