package cm.apirecetamedica.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_receta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleReceta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_receta_medica", nullable = false)
    private RecetaMedica recetaMedica;

    @Column(name = "id_medicamento", nullable = false)
    private Long idMedicamento;

    @Column(name = "dosis", nullable = false)
    private String dosis;

    @Column(name = "frecuencia", nullable = false)
    private String frecuencia;

    @Column(name = "via_administracion", nullable = false)
    private String viaAdministracion;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

}
