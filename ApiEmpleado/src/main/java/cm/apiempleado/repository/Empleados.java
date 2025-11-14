package cm.apiempleado.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "empleados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empleados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombres", nullable = false)
    private String nombres;

    @Column(name = "apellidos", nullable = false)
    private String apellidos;

    @Enumerated(EnumType.STRING)
    @Column(name = "cargo", nullable = false)
    private Cargos cargo;

    @Column(name = "dni", nullable = false, unique = true, length = 8)
    private String dni;

    @Column(name = "telefono", nullable = false, unique = true, length = 9)
    private String telefono;

    @Column(name = "correo", nullable = false, unique = true)
    private String correo;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @ElementCollection
    @CollectionTable(
            name = "empleado_especialidad",
            joinColumns = @JoinColumn(name = "empleado_id")
    )
    @Column(name = "especialidad_id")
    private Set<Long> especialidadIds = new HashSet<>();

    public enum Cargos{
        MEDICO,
        PERSONAL_ADMINISTRATIVO,
        ENFERMERA,
        CAJERO
    }

}
