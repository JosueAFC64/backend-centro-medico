package cm.apidisponibilidad.service;

import cm.apidisponibilidad.client.empleado.EmpleadoClientResponse;
import cm.apidisponibilidad.client.empleado.EmpleadoFeignClient;
import cm.apidisponibilidad.client.especialidad.EspecialidadFeignClient;
import cm.apidisponibilidad.client.especialidad.EspecialidadResponse;
import cm.apidisponibilidad.dto.DisponibilidadRequest;
import cm.apidisponibilidad.dto.DisponibilidadResponse;
import cm.apidisponibilidad.repository.Disponibilidad;
import cm.apidisponibilidad.repository.DisponibilidadRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisponibilidadService {

    private final EmpleadoFeignClient empleadoClient;
    private final EspecialidadFeignClient especialidadClient;
    private final DisponibilidadRepository repository;

    // SERVICIOS CRUD

    /**
     * Registra una nueva {@link Disponibilidad} en la base de datos
     *
     * @param request Objeto {@link DisponibilidadRequest} que contiene los datos requeridos para registrar la
     *                disponibilidad
     * @return Objeto {@link DisponibilidadResponse} que contiene los datos de la disponibilidad ya registrada
     */
    @Transactional
    public DisponibilidadResponse registrar(DisponibilidadRequest request) {
        log.info("Inicio de proceso de registro de disponibilidad para médico con ID: {}", request.idMedico());

        log.debug("Realizando validaciones");
        validarDisponibilidad(
                null,
                request.idMedico(),
                request.idEspecialidad(),
                request.fecha(),
                request.hora_inicio(),
                request.hora_fin()
        );
        log.debug("Validaciones sin excepciones encontradas");

        log.debug("Creando entidad Disponibilidad");
        Disponibilidad d = Disponibilidad.builder()
                .idMedico(request.idMedico())
                .idEspecialidad(request.idEspecialidad())
                .fecha(request.fecha())
                .hora_inicio(request.hora_inicio())
                .hora_fin(request.hora_fin())
                .build();

        repository.save(d);
        log.info("Disponibilidad registrada correctamente con ID: {}", d.getId());

        return toResponse(d);
    }

    /**
     * Lista todas las disponibilidades de la base de datos
     *
     * @return Una lista de objetos {@link DisponibilidadResponse}
     */
    @Transactional(readOnly = true)
    public List<DisponibilidadResponse> listar() {
        log.info("Inicio de proceso de listar disponibilidades");

        List<Disponibilidad> disponibilidades = repository.findAll();

        log.info("Disponibilidades listadas correctamente: {}", disponibilidades.size());

        return disponibilidades.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Busca todas las disponibilidades de un médico por su ID
     *
     * @param idMedico Identificador único del médico
     * @return Objeto {@link DisponibilidadResponse} que contiene los datos de la disponibilidad
     */
    @Transactional(readOnly = true)
    public List<DisponibilidadResponse> buscarTodosPorMedico(Long idMedico) {
        log.info("Inicio de proceso de búsqueda con idMedico: {}", idMedico);

        List<Disponibilidad> disponibilidad = repository.findAllByIdMedico(idMedico);

        log.info("Disponibilidad con idMedico: {} encontrada", idMedico);

        return disponibilidad.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Actualiza los datos de un objeto {@link Disponibilidad}
     *
     * @param request Objeto {@link DisponibilidadRequest que contiene los datos actualizados}
     * @param id Identificador único de la disponibilidad
     * @return Objeto {@link DisponibilidadResponse} que contiene los datos ya actualizados
     */
    @Transactional
    public DisponibilidadResponse actualizar(DisponibilidadRequest request, Long id) {
        log.info("Inicio de proceso de actualización para ID: {}", id);

        log.debug("Realizando validaciones");
        validarDisponibilidad(
                id,
                request.idMedico(),
                request.idEspecialidad(),
                request.fecha(),
                request.hora_inicio(),
                request.hora_fin()
        );
        log.debug("Validaciones sin excepciones encontradas");

        Disponibilidad d = repository.findById(id)
                .orElseThrow(() -> {
                   log.warn("Disponibilidad con ID: {} no encontrada", id);
                   return new EntityNotFoundException("Disponibilidad con ID: " + id + " no encontrada");
                });
        log.debug("Disponibilidad con ID: {} encontrada", id);

        if (!d.getIdEspecialidad().equals(request.idEspecialidad())) {
            log.debug("Actualizando idEspecialidad de '{}' a '{}'", d.getIdEspecialidad(), request.idEspecialidad());
            d.setIdEspecialidad(request.idEspecialidad());
        }

        if (!d.getFecha().equals(request.fecha())) {
            log.debug("Actualizando fecha de '{}' a '{}'", d.getFecha(), request.fecha());
            d.setFecha(request.fecha());
        }

        if (!d.getHora_inicio().equals(request.hora_inicio())) {
            log.debug("Actualizando hora de inicio de '{}' a '{}'", d.getHora_inicio(), request.hora_inicio());
            d.setHora_inicio(request.hora_inicio());
        }

        if (!d.getHora_fin().equals(request.hora_fin())) {
            log.debug("Actualizando hora de fin de '{}' a '{}'", d.getHora_fin(), request.hora_fin());
            d.setHora_fin(request.hora_fin());
        }

        repository.save(d);
        log.info("Disponibilidad con ID: {} actualizada", id);

        return toResponse(d);
    }

    @Transactional
    public void eliminar(Long id) {
        log.info("Inicio de proceso de eliminación con ID: {}", id);

        Disponibilidad d = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Disponibilidad con ID: {} no encontrada", id);
                    return new EntityNotFoundException("Especialidad con ID: " + id + " no encontrada");
                });

        repository.delete(d);

        log.info("Disponibilidad con ID: {} eliminada correctamente", id);
    }

    // SERVICIOS PARA OBTENER DATOS DE OTROS MICROSERVICIOS

    /**
     * Obtiene el ID y nombre de un empleado a través del cliente {@code empleadoClient},
     * si el empleado no existe (404), devuelve error.
     *
     * @param id Identificador único del empleado
     * @return Objeto {@link EmpleadoClientResponse} que contiene el ID y nombre del empleado
     * @throws IllegalArgumentException Si el ID es inválido
     */
    private EmpleadoClientResponse obtenerEmpleadoNombre(Long id) {
        if (id == null || id <= 0) {
            log.error("Intento de obtener nombre con ID inválido: {}", id);
            throw new IllegalArgumentException("Id inválido");
        }

        return empleadoClient.obtenerNombre(id);

    }

    /**
     * Obtiene el ID y nombre de una especialidad a través del cliente {@code especialidadClient},
     * si la especialidad no existe (404), devuelve error.
     *
     * @param id Identificador único de la especialidad
     * @return Objeto {@link EspecialidadResponse} que contiene el ID y nombre de la especialidad
     * @throws IllegalArgumentException Si el ID es inválido
     */
    private EspecialidadResponse obtenerEspecialidadNombre(Long id) {
        if (id == null || id <= 0) {
            log.error("Intento de obtener nombre con ID inválido: {}", id);
            throw new IllegalArgumentException("Id inválido");
        }

        return especialidadClient.obtenerEspecialidad(id);
    }

    // AUXILIARES

    /**
     * Realiza las siguientes validaciones para {@link Disponibilidad}:
     * <ul>
     *     <li>Que {@code hora_fin} sea posterior a {@code hora_inicio}</li>
     *     <li>Que {@code fecha} sea una fecha futura</li>
     *     <li>Que no haya solapamiento con otra disponibilidad</li>
     * </ul>
     *
     * @param idDisponibilidad Identificador único de la disponibilidad
     * @param idMedico Identificador único del médico asociado
     * @param idEspecialidad Identificador único de la especialidad asociada
     * @param fecha Fecha (yyyy-MM-dd) de la disponibilidad
     * @param horaInicio Hora (HH:mm:ss) de inicio de la disponibilidad
     * @param horaFin Hora (HH:mm:ss) de fin de la disponibilidad
     * @throws IllegalArgumentException Si alguna validación no se cumple
     */
    public void validarDisponibilidad(
            Long idDisponibilidad,
            Long idMedico,
            Long idEspecialidad,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin
    ) {
        log.debug("Validando rango de horas de disponibilidad");
        if (!horaFin.isAfter(horaInicio)) {
            log.warn("Intento de registro inválido, hora de fin: {} es anterior a hora de inicio: {}", horaFin, horaInicio);
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }

        log.debug("Validando fecha de disponibilidad");
        if (!fecha.isAfter(LocalDate.now())) {
            log.warn("Intento de registro inválido, fecha: {} no es una fecha futura", fecha);
            throw new IllegalArgumentException("La disponibilidad debe ser para una fecha futura");
        }

        log.debug("Validando solapamiento por rango de horas y fecha");
        boolean existeSolapamiento = (idDisponibilidad == null)
                ? repository.existeSolapamiento(
                        idMedico, idEspecialidad, fecha, horaInicio, horaFin)
                : repository.existeSolapamientoExcluyendoId(
                        idDisponibilidad, idMedico, idEspecialidad, fecha, horaInicio, horaFin);

        if (existeSolapamiento) {
            log.warn("Ya existe una disponibilidad registrada para ese médico, especialidad, fecha y rango horario");
            throw new IllegalArgumentException("Ya existe una disponibilidad registrada en ese rango");
        }
    }

    // MAPEADORES A DTO

    private DisponibilidadResponse toResponse(Disponibilidad disponibilidad) {

        EmpleadoClientResponse medico = obtenerEmpleadoNombre(disponibilidad.getIdMedico());

        EspecialidadResponse especialidad = obtenerEspecialidadNombre(disponibilidad.getIdEspecialidad());

        return new DisponibilidadResponse(
                disponibilidad.getId(),
                medico,
                especialidad,
                disponibilidad.getFecha(),
                disponibilidad.getHora_inicio(),
                disponibilidad.getHora_fin()
        );
    }

}
