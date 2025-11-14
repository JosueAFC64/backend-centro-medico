package cm.apicitamedica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Long> {
    Optional<CitaMedica> findByIdHorarioAndIdDetalleHorario(Long idHorario, Long idDetalleHorario);

    /**
     * Verifica si existe solapamiento al crear una nueva cita médica
     * @param idHorario ID del horario
     * @param idDetalle ID del detalle
     * @param dniPaciente DNI del paciente (opcional)
     * @return true si existe solapamiento, false en caso contrario
     */
    @Query("SELECT COUNT(c) > 0 FROM CitaMedica c WHERE " +
            "c.idHorario = :idHorario AND " +
            "c.idDetalleHorario = :idDetalle AND " +
            "(:dniPaciente IS NULL OR c.dniPaciente = :dniPaciente) AND " +
            "c.estado <> cm.apicitamedica.repository.CitaMedica.EstadoCitaMedica.CANCELADA")
    boolean existeSolapamiento(@Param("idHorario") Long idHorario,
                               @Param("idDetalle") Long idDetalle,
                               @Param("dniPaciente") String dniPaciente);

    /**
     * Verifica si existe solapamiento al actualizar una cita médica, excluyendo la cita actual
     * @param idHorario ID del horario
     * @param idDetalle ID del detalle
     * @param dniPaciente DNI del paciente (opcional)
     * @param idCitaExcluir ID de la cita a excluir (la que se está actualizando)
     * @return true si existe solapamiento, false en caso contrario
     */
    @Query("SELECT COUNT(c) > 0 FROM CitaMedica c WHERE " +
            "c.idHorario = :idHorario AND " +
            "c.idDetalleHorario = :idDetalle AND " +
            "(:dniPaciente IS NULL OR c.dniPaciente = :dniPaciente) AND " +
            "c.id <> :idCitaExcluir AND " +
            "c.estado<> cm.apicitamedica.repository.CitaMedica.EstadoCitaMedica.CANCELADA")
    boolean existeSolapamientoExcluyendoId(@Param("idHorario") Long idHorario,
                                           @Param("idDetalle") Long idDetalle,
                                           @Param("dniPaciente") String dniPaciente,
                                           @Param("idCitaExcluir") Long idCitaExcluir);

    Optional<CitaMedica> findByDniPaciente(String dniPaciente);
}
