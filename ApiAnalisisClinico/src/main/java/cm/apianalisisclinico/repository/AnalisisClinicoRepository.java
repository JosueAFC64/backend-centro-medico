package cm.apianalisisclinico.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalisisClinicoRepository extends JpaRepository<AnalisisClinico, Long> {
    Optional<AnalisisClinico> findByIdAtencion(Long idAtencion);
}
