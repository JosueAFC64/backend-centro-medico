package cm.apimedicamentos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicamentosRepository extends JpaRepository<Medicamentos, Long> {

    boolean existsByNombreContainingIgnoreCase(String nombre);

}
