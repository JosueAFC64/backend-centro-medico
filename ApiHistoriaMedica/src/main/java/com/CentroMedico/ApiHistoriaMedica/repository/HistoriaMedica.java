package com.CentroMedico.ApiHistoriaMedica.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "historia_medica")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriaMedica {
    @Id
    @Column(name = "id_historia_medica", nullable = false, unique = true, length = 8)
    private String idHistoriaMedica;

    @Column(nullable = false)
    private Long idPaciente;

    @Column(nullable = false, columnDefinition = "NUMERIC(5, 2)")
    private Double peso;

    @Column(nullable = false, columnDefinition = "NUMERIC(5, 2)")
    private Double talla;

    @Column
    private Integer edad;

    @Column(name = "tipo_sangre", length = 10)
    private String tipoSangre;

    @Column(name = "alergias")
    private String alergias;

    @Column(name = "antecedentes_familiares", length = 500)
    private String antecedentesFamiliares;

    @Column(name = "antecedentes_personales", length = 500)
    private String antecedentesPersonales;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @ElementCollection
    @CollectionTable(
            name = "historia_atenciones",
            joinColumns = @JoinColumn(name = "historia_id")
    )
    @Column(name = "atenciones_ids")
    private Set<Long> atencionesIds;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDate.now();
    }

    public void agregarAtencion(Long idAtencion){
        if (idAtencion == null || idAtencion <= 0) {
            throw new IllegalArgumentException("El ID de la atención no puede ser negativo o nulo");
        }

        atencionesIds.add(idAtencion);
    }
}
