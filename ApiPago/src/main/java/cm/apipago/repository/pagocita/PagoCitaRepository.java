package cm.apipago.repository.pagocita;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PagoCitaRepository extends JpaRepository<PagoCita, Long> {
    Optional<PagoCita> findByDniPaciente(String dniPaciente);

    List<PagoCita> findAllByDniPaciente(String dniPaciente);
}
