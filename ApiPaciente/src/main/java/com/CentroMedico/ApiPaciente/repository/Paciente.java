package com.CentroMedico.ApiPaciente.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "paciente")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPaciente;

    @Column(nullable = false, length = 30)
    private String nombres;

    @Column(nullable = false, length = 30)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @Column(length = 9)
    private String telefono;

    @Column(length = 50)
    private String correo;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(length = 9)
    private String telefonoEmergencia;

    @Column(length = 30)
    private String contactoEmergencia;

    @Column(length = 100)
    private String direccion;

    public void validar() {
        if (fechaNacimiento == null || fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento es inválida o es una fecha futura.");
        }
    }
}
