package cm.apirecetamedica.repository;

import cm.apirecetamedica.dto.detallereceta.DetalleRecetaRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receta_medica")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecetaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dni_paciente", nullable = false)
    private String dniPaciente;

    @Column(name = "id_medico", nullable = false)
    private Long idMedico;

    @Column(name = "id_atencion", nullable = false)
    private Long idAtencion;

    @Column(name = "fecha_solicitud",nullable = false)
    private LocalDate fechaSolicitud;

    @OneToMany(
            mappedBy = "recetaMedica",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<DetalleReceta> detalles = new ArrayList<>();

    /**
     * Agrega un detalle a la Receta Médica
     */
    public void agregarDetalle(DetalleRecetaRequest request) {
        DetalleReceta detalle = DetalleReceta.builder()
                .recetaMedica(this)
                .idMedicamento(request.idMedicamento())
                .dosis(request.dosis())
                .frecuencia(request.frecuencia())
                .viaAdministracion(request.viaAdministracion())
                .cantidad(request.cantidad())
                .build();

        detalles.add(detalle);
    }

    /**
     * Agrega una Lista de Detalles a la Receta Médica
     */
    public void agregarDetalles(List<DetalleRecetaRequest> requests) {
        requests.forEach(this::agregarDetalle);
    }

}
