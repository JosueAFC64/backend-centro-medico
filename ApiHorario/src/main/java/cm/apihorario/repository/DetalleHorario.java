package cm.apihorario.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "detalle_horario", indexes = {
        @Index(name = "idx_horario", columnList = "id_horario"),
        @Index(name = "idx_especialidad", columnList = "id_especialidad"),
        @Index(name = "idx_estado", columnList = "estado"),
        @Index(name = "idx_horario_horas", columnList = "id_horario, hora_inicio, hora_fin")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_horario", nullable = false)
    private Horario horario;

    @Column(name = "id_empleado", nullable = false)
    private Long idEmpleado;

    @Column(name = "nro_consultorio", nullable = false)
    private String nroConsultorio;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private  EstadoDetalleHorario estado = EstadoDetalleHorario.DISPONIBLE;

    @Column(name = "id_cita")
    private Long idCita;

    public enum EstadoDetalleHorario {
        DISPONIBLE,
        OCUPADO,
        BLOQUEADO
    }

    /**
     * Marca el slot como ocupado por una cita
     */
    public void ocupar(Long idCita) {
        if (this.estado != EstadoDetalleHorario.DISPONIBLE) {
            throw new IllegalStateException(
                    "Solo se pueden ocupar slots disponibles. Estado actual: " + this.estado
            );
        }

        this.estado = EstadoDetalleHorario.OCUPADO;
        this.idCita = idCita;
    }

    /**
     * Libera el slot (cancela la cita)
     */
    public void liberar() {
        if (this.estado != EstadoDetalleHorario.OCUPADO) {
            throw new IllegalStateException(
              "Solo se pueden liberar slots ocupados. Estado actual: " + this.estado
            );
        }

        this.estado = EstadoDetalleHorario.DISPONIBLE;
        this.idCita = null;
    }

    /**
     * Bloquea el slot
     */
    public void bloquear() {
        if (this.estado == EstadoDetalleHorario.OCUPADO) {
            throw new IllegalStateException(
                    "No se puede bloquear un slot ocupado por una cita"
            );
        }

        this.estado = EstadoDetalleHorario.BLOQUEADO;
    }

    /**
     * Desbloquea el slot
     */
    public void desbloquear() {
        if (this.estado != EstadoDetalleHorario.BLOQUEADO) {
            throw new IllegalStateException(
                    "Solo se pueden desbloquear slots bloqueados"
            );
        }

        this.estado = EstadoDetalleHorario.DISPONIBLE;
    }

    /**
     * Valida que el detalle tenga sentido
     */
    public void validar() {
        if (horaFin.isBefore(horaInicio) || horaFin.equals(horaInicio)) {
            throw new IllegalArgumentException(
                    "La hora de fin debe ser posterior a la hora de inicio"
            );
        }

        if (estado == EstadoDetalleHorario.OCUPADO && idCita == null) {
            throw new IllegalStateException(
                    "Un slot ocupado debe estar asociado a una cita"
            );
        }

        if (estado != EstadoDetalleHorario.OCUPADO && idCita != null) {
            throw new IllegalStateException(
                    "Solo slots ocupados pueden estar asociados a una cita"
            );
        }
    }

    /**
     * Verifica si el slot está disponible
     */
    public boolean estaDisponible() {
        return this.estado == EstadoDetalleHorario.DISPONIBLE;
    }

    /**
     * Calcula la duración del slot en minutos
     */
    public int getDuracionMinutos() {
        return (int) java.time.Duration.between(horaInicio, horaFin).toMinutes();
    }

}
