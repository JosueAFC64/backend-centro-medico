package cm.apidisponibilidad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

    // Consulta para verificar solapamiento específico
    @Query("SELECT COUNT(d) > 0 FROM Disponibilidad d WHERE " +
            "d.idMedico = :idMedico AND " +
            "d.idEspecialidad = :idEspecialidad AND " +
            "d.fecha = :fecha AND " +
            "(:horaInicio < d.hora_fin AND :horaFin > d.hora_inicio)")
    boolean existeSolapamiento(@Param("idMedico") Long idMedico,
                               @Param("idEspecialidad") Long idEspecialidad,
                               @Param("fecha") LocalDate fecha,
                               @Param("horaInicio") LocalTime horaInicio,
                               @Param("horaFin") LocalTime horaFin);

    @Query("SELECT COUNT(d) > 0 FROM Disponibilidad d WHERE " +
            "d.idMedico = :idMedico AND " +
            "d.idEspecialidad = :idEspecialidad AND " +
            "d.fecha = :fecha AND " +
            "(:horaInicio < d.hora_fin AND :horaFin > d.hora_inicio) AND " +
            "d.id <> :idActual")
    boolean existeSolapamientoExcluyendoId(@Param("idActual") Long idActual,
                                           @Param("idMedico") Long idMedico,
                                           @Param("idEspecialidad") Long idEspecialidad,
                                           @Param("fecha") LocalDate fecha,
                                           @Param("horaInicio") LocalTime horaInicio,
                                           @Param("horaFin") LocalTime horaFin);


    List<Disponibilidad> findAllByIdMedico(Long idMedico);
}
