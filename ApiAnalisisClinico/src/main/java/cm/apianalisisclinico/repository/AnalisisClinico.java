package cm.apianalisisclinico.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "analisis_clinico")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalisisClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dni_paciente", nullable = false)
    private String dniPaciente;

    @Column(name = "id_medico", nullable = false)
    private Long idMedico;

    @Column(name = "fecha_solicitud",nullable = false)
    private LocalDate fechaSolicitud;

    @OneToMany(
            mappedBy = "analisisClinico",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<DetalleAnalisis> detalles = new ArrayList<>();

    /**
     * Agrega un detalle a Análisis Clínico
     */
    public void agregarDetalle(Long idTipoAnalisis) {
        DetalleAnalisis detalle = DetalleAnalisis.builder()
                .idTipoAnalisis(idTipoAnalisis)
                .analisisClinico(this)
                .build();

        detalles.add(detalle);
    }

    /**
     * Agrega detalles al Análisis Clínico
     */
    public void agregarDetalles(List<Long> tipoAnalisisIds){
        tipoAnalisisIds.forEach(this::agregarDetalle);
    }

}
