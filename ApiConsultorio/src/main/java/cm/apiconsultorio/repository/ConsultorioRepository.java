package cm.apiconsultorio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsultorioRepository extends JpaRepository<Consultorio, Long> {
    boolean existsByNroConsultorio(String nroConsultorio);

    boolean existsByNroConsultorioAndIdNot(String nroConsultorio, Long id);

    void deleteByNroConsultorio(String nroConsultorio);

    Optional<Consultorio> findByNroConsultorio(String nroConsultorio);
}
