package cm.apiatencionmedica.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "atencion_medica")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtencionMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cita", nullable = false)
    private Long idCita;

    @Column(name = "fecha_atencion", nullable = false)
    private LocalDate fechaAtencion;

    @Builder.Default
    @Column(name = "hora_atencion", nullable = false)
    private LocalTime horaAtencion = LocalTime.now();

    @Column(name = "diagnostico", nullable = false, length = 500)
    private String diagnostico;

    @Column(name = "tratamiento", nullable = false, length = 500)
    private String tratamiento;

    @Column(name = "observaciones", nullable = false, length = 500)
    private String observaciones;

    // POR SI EL MÉDICO ORIGINAL NO HACE LA ATENCIÓN MÉDICA

    @Column(name = "id_medico_ejecutor", nullable = false)
    private Long idMedicoEjecutor;

    @Builder.Default
    @Column(name = "es_medico_reemplazo")
    private Boolean esMedicoReemplazo = Boolean.FALSE;


}
