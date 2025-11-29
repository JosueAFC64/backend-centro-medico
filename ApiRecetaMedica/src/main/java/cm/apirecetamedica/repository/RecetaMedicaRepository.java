package cm.apirecetamedica.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecetaMedicaRepository extends JpaRepository<RecetaMedica, Long> {
    Optional<RecetaMedica> findByIdAtencion(Long idAtencion);

    Long id(Long id);
}
