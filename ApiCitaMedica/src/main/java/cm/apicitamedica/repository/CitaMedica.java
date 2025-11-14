package cm.apicitamedica.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "citas_medicas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dni_paciente", nullable = false)
    private String dniPaciente;

    @Column(name = "id_horario", nullable = false)
    private Long idHorario;

    @Column(name = "id_detalle_horario", nullable = false)
    private Long idDetalleHorario;

    @Column(name = "costo", nullable = false)
    private BigDecimal costo;

    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoCitaMedica estado = EstadoCitaMedica.PENDIENTE;

    public enum EstadoCitaMedica {
        PENDIENTE,
        COMPLETADA,
        CANCELADA
    }

    public void cancelarCita() {
        if (this.estado != EstadoCitaMedica.PENDIENTE ) {
            throw new IllegalStateException(
                    "Solo se puede cancelar una Cita pendiente. Estado actual: " + this.estado
            );
        }

        this.estado = EstadoCitaMedica.CANCELADA;
    }

    public void completarCita() {
        if (this.estado != EstadoCitaMedica.PENDIENTE ) {
            throw new IllegalStateException(
                    "Solo se puede completar una Cita pendiente. Estado actual: " + this.estado
            );
        }

        this.estado = EstadoCitaMedica.COMPLETADA;
    }

}
