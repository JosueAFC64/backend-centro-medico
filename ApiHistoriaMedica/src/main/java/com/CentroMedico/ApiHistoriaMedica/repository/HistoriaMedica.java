package com.CentroMedico.ApiHistoriaMedica.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

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

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDate.now();
    }
}
