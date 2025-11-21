package cm.apiatencionmedica.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AtencionMedicaRepository extends JpaRepository<AtencionMedica, Long> {
    Optional<AtencionMedica> findByIdCita(Long idCita);
}
