package cm.apihorario.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "horario", indexes = {
        @Index(name = "idx_empleado_fecha", columnList = "id_empleado, fecha"),
        @Index(name = "idx_fecha", columnList = "fecha")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_empleado", nullable = false)
    private Long idEmpleado;

    @Column(name = "id_especialidad", nullable = false)
    private Long idEspecialidad;

    @Column(name = "nro_consultorio", nullable = false)
    private String nroConsultorio;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "duracion_slot_minutos")
    @Builder.Default
    private Integer duracionSlotMinutos = 30;

    @OneToMany(
            mappedBy = "horario",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<DetalleHorario> detalles = new ArrayList<>();

    /**
     * Agrega un detalle al horario
     */
    public void agregarDetalle(DetalleHorario detalle) {
        detalles.add(detalle);
        detalle.setHorario(this);
    }

    /**
     * Genera slots automáticamente basándose en {@code hora_inicio}, {@code hora_fin} y {@code duración}
     */
    public void generarSlots() {
        LocalTime horaActual = this.horaInicio;

        while (horaActual.isBefore(this.horaFin)) {
            LocalTime horaFinSlot = horaActual.plusMinutes(this.duracionSlotMinutos);

            if (horaFinSlot.isAfter(this.horaFin)) {
                break;
            }

            DetalleHorario detalle = DetalleHorario.builder()
                    .idEmpleado(this.idEmpleado)
                    .nroConsultorio(this.nroConsultorio)
                    .horaInicio(horaActual)
                    .horaFin(horaFinSlot)
                    .estado(DetalleHorario.EstadoDetalleHorario.DISPONIBLE)
                    .build();

            this.agregarDetalle(detalle);

            horaActual = horaFinSlot;
        }
    }

    /**
     * Valida que el horario tenga sentido
     */
    public void validar() {
        if (horaFin.isBefore(horaInicio) || horaFin.equals(horaInicio)) {
            throw new IllegalArgumentException(
              "La hora de fin debe ser posterior a la hora de inicio"
            );
        }

        if (duracionSlotMinutos != null && duracionSlotMinutos <= 0) {
            throw new IllegalArgumentException(
              "La duración del slot debe ser mayor a 0"
            );
        }

        if (!fecha.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha del horario debe ser futura"
            );
        }
    }

    /**
     * Cuenta cuantos slots están disponibles
     */
    public long contarSlotsDisponibles() {
        return detalles.stream()
                .filter(d -> d.getEstado() == DetalleHorario.EstadoDetalleHorario.DISPONIBLE)
                .count();
    }

    /**
     * Cuenta cuantos slots están ocupados
     */
    public long contarSlotsOcupados() {
        return detalles.stream()
                .filter(d -> d.getEstado() == DetalleHorario.EstadoDetalleHorario.OCUPADO)
                .count();
    }

    /**
     * Cuenta cuantos slots están bloqueados
     */
    public long contarSlotsBloqueados() {
        return detalles.stream()
                .filter(d -> d.getEstado() == DetalleHorario.EstadoDetalleHorario.BLOQUEADO)
                .count();
    }

    /**
     * Verifica si el horario está completamente ocupado
     */
    public boolean estaCompleto() {
        return detalles.stream()
                .noneMatch(d -> d.getEstado() == DetalleHorario.EstadoDetalleHorario.DISPONIBLE);
    }

}
