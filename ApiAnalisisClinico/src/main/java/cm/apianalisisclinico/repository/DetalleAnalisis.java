package cm.apianalisisclinico.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_analisis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleAnalisis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_analisis_clinico", nullable = false)
    private AnalisisClinico analisisClinico;

    @Column(name = "id_tipo_analisis", nullable = false)
    private Long idTipoAnalisis;


}
