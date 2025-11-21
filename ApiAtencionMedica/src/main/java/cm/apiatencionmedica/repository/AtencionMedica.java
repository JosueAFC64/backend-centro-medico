package cm.apiatencionmedica.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "diagnostico", nullable = false, length = 500)
    private String diagnostico;

    @Column(name = "tratamiento", nullable = false, length = 500)
    private String tratamiento;

    @Column(name = "observaciones", nullable = false, length = 500)
    private String observaciones;

}
