package cm.apiespecialidad.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {
    boolean existsByNombreAndIdNot(String nombre, Long id);


    boolean existsByNombreContainingIgnoreCase(String nombre);
}
