package com.CentroMedico.ApiHistoriaMedica.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HistoriaMedicaRepository extends JpaRepository<HistoriaMedica, String> {

    Optional<HistoriaMedica> findByIdPaciente(Long idPaciente);

    boolean existsByIdPaciente(Long idPaciente);
}
