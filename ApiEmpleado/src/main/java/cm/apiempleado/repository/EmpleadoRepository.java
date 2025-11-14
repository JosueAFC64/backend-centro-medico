package cm.apiempleado.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpleadoRepository extends JpaRepository<Empleados, Long> {
    boolean existsByDniAndIdNot(String dni, Long id);

    boolean existsByTelefonoAndIdNot(String telefono, Long id);

    boolean existsByCorreoAndIdNot(String correo, Long id);

    List<Empleados> findAllByCargo(Empleados.Cargos cargo);

    List<Empleados> findByCargoAndEspecialidadIdsContaining(Empleados.Cargos cargo, Long especialidadId);

    boolean existsByDni(String dni);

    boolean existsByTelefono(String telefono);

    boolean existsByCorreo(String correo);
}
