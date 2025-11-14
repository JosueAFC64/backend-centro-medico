package cm.apihorario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Long> {

    /**
     * Busca horarios por empleado y fecha
     */
    @Query("SELECT h FROM Horario h WHERE h.idEmpleado = :idEmpleado AND h.fecha = :fecha")
    List<Horario> findByEmpleadoYFecha(@Param("idEmpleado") Long idEmpleado, @Param("fecha") LocalDate fecha);

    /**
     * Valida si es que existe solapamiento de horarios
     */
    @Query("SELECT COUNT(h) > 0 FROM Horario h WHERE " +
            "h.fecha = :fecha AND " +
            "(:horaInicio < h.horaFin AND :horaFin > h.horaInicio) AND " +
            "(h.idEmpleado = :idMedico OR h.nroConsultorio = :nroConsultorio)")
    boolean existeSolapamiento(@Param("idMedico") Long idMedico,
                               @Param("nroConsultorio") String nroConsultorio,
                               @Param("fecha") LocalDate fecha,
                               @Param("horaInicio") LocalTime horaInicio,
                               @Param("horaFin") LocalTime horaFin);

}
