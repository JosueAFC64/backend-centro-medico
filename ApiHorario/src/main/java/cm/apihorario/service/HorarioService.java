package cm.apihorario.service;

import cm.apihorario.client.citamedica.CitaMedicaFeignClient;
import cm.apihorario.client.citamedica.CitaMedicaFeignResponse;
import cm.apihorario.client.consultorio.ConsultorioFeignClient;
import cm.apihorario.client.consultorio.ConsultorioResponse;
import cm.apihorario.client.empleado.EmpleadoClientResponse;
import cm.apihorario.client.empleado.EmpleadoFeignClient;
import cm.apihorario.client.especialidad.EspecialidadFeignClient;
import cm.apihorario.client.especialidad.EspecialidadResponse;
import cm.apihorario.dto.SlotClientResponse;
import cm.apihorario.dto.HorarioRequest;
import cm.apihorario.dto.HorarioResponse;
import cm.apihorario.repository.DetalleHorario;
import cm.apihorario.repository.Horario;
import cm.apihorario.repository.HorarioRepository;
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
@Transactional
public class HorarioService {

    private final HorarioRepository repository;
    private final EmpleadoFeignClient empleadoClient;
    private final EspecialidadFeignClient especialidadClient;
    private final ConsultorioFeignClient consultorioClient;
    private final CitaMedicaFeignClient citaMedicaClient;

    // SERVICIOS CRUD

    /**
     * Registra un nuevo horario con slots automáticos en la base de datos
     *
     * @param request Objeto {@link HorarioRequest} que contiene los datos requeridos para registrar el horario
     * @return Objeto {@link HorarioResponse} que contiene los datos del horario ya registrado
     * @throws IllegalArgumentException Si existe solapamiento con otro horario para el mismo médico
     * en el rango de horas proporcionado
     */
    @Transactional
    public HorarioResponse registrar(HorarioRequest request) {
        log.info("Proceso de registro para médico: {} iniciada", request.idEmpleado());

        log.debug("Creando entidad Horario");
        Horario horario = Horario.builder()
                .idEmpleado(request.idEmpleado())
                .idEspecialidad(request.idEspecialidad())
                .nroConsultorio(request.nro_consultorio())
                .fecha(request.fecha())
                .horaInicio(request.horaInicio())
                .horaFin(request.horaFin())
                .duracionSlotMinutos(request.duracionSlotMinutos())
                .build();

        log.debug("Realizando validaciones");
        horario.validar();

        existeSolapamiento(
                horario.getIdEmpleado(),
                horario.getNroConsultorio(),
                horario.getFecha(),
                horario.getHoraInicio(),
                horario.getHoraFin()
        );
        log.debug("Validaciones realizadas correctamente");

        log.debug("Generando slots automáticamente");
        horario.generarSlots();

        Horario horarioGuardado = repository.save(horario);
        log.debug("Horario con ID: {} creado correctamente", horario.getId());

        return toResponse(horarioGuardado);
    }

    /**
     * Lista todos los horarios de la base de datos
     *
     * @return Lista de objetos {@link HorarioResponse} que contiene los datos de los horarios
     */
    @Transactional(readOnly = true)
    public List<HorarioResponse> listar() {
        log.info("Inicio de proceso de listar");

        List<Horario> horarios = repository.findAll();

        log.info("Horarios listados correctamente: {}", horarios.size());

        return horarios
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Busca horarios por empleado y fecha
     *
     * @return Lista de objetos {@link HorarioResponse} que contiene los datos de los horarios
     */
    @Transactional(readOnly = true)
    public List<HorarioResponse> buscarPorEmpleadoYFecha(Long idEmpleado, LocalDate fecha) {
        log.info("Inicio de proceso de listar por médico y fecha");

        List<Horario> horarios = repository.findByEmpleadoYFecha(idEmpleado, fecha);

        log.info("Horarios listados correctamente: {}", horarios.size());

        return horarios
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Elimina un horario y sus slots (permanente)
     *
     * @throws IllegalArgumentException Si no existe un horario en la base de datos con el ID brindado
     */
    @Transactional
    public void eliminar(Long id) {
        log.info("Inicio de proceso de eliminación para ID: {}", id);

        if (!repository.existsById(id)) {
            log.warn("Horario con ID: {} no encontrado", id);
            throw new IllegalArgumentException("Horario no encontrado con ID: " + id);
        }

        repository.deleteById(id);
        log.info("Horario eliminado correctamente");
    }

    // OTROS SERVICIOS

    /**
     * Ocupa un slot específico para una cita
     *
     * @throws IllegalArgumentException Si:
     * <ul>
     *     <li>No existe un horario en la base de datos con el ID brindado</li>
     *     <li>No existe un detalle/slot en la base de datos con el ID brindado</li>
     * </ul>
     */
    @Transactional
    public void ocuparSlot(Long idHorario, Long idDetalle, Long idCita) {
        log.info("Inicio de proceso de ocupar slot: {}", idDetalle);

        Horario horario = repository.findById(idHorario)
                .orElseThrow(() -> {
                    log.warn("Horario con ID: {} no encontrado", idHorario);
                    return new IllegalArgumentException("Horario no encontrado con ID: " + idHorario);
                });

        DetalleHorario detalle = horario.getDetalles().stream()
                .filter(d -> d.getId().equals(idDetalle))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Detalle con ID: {} no encontrado", idDetalle);
                    return new IllegalArgumentException("Detalle no encontrado con ID: " + idDetalle);
                });

        detalle.ocupar(idCita);
        repository.save(horario);
        log.info("Slot ocupado correctamente");
    }

    /**
     * Libera un slot (cancela una cita)
     *
     * @throws IllegalArgumentException Si:
     * <ul>
     *     <li>No existe un horario en la base de datos con el ID brindado</li>
     *     <li>No existe un detalle/slot en la base de datos con el ID brindado</li>
     * </ul>
     */
    @Transactional
    public void liberarSlot(Long idHorario, Long idDetalle) {
        log.info("Inicio de proceso de liberar slot: {}", idDetalle);

        Horario horario = repository.findById(idHorario)
                .orElseThrow(() -> {
                    log.warn("Horario con ID: {} no encontrado", idHorario);
                    return new IllegalArgumentException("Horario no encontrado con ID: " + idHorario);
                });

        DetalleHorario detalle = horario.getDetalles().stream()
                .filter(d -> d.getId().equals(idDetalle))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Detalle con ID: {} no encontrado", idDetalle);
                    return new IllegalArgumentException("Detalle no encontrado con ID: " + idDetalle);
                });

        detalle.liberar();
        repository.save(horario);
        log.info("Slot liberado correctamente");
    }

    /**
     * Bloquea un slot
     *
     * @throws IllegalArgumentException Si:
     * <ul>
     *     <li>No existe un horario en la base de datos con el ID brindado</li>
     *     <li>No existe un detalle/slot en la base de datos con el ID brindado</li>
     * </ul>
     */
    @Transactional
    public void bloquearSlot(Long idHorario, Long idDetalle) {
        log.info("Inicio de proceso de bloquear slot: {}", idDetalle);

        Horario horario = repository.findById(idHorario)
                .orElseThrow(() -> {
                    log.warn("Horario con ID: {} no encontrado", idHorario);
                    return new IllegalArgumentException("Horario no encontrado con ID: " + idHorario);
                });

        DetalleHorario detalle = horario.getDetalles().stream()
                .filter(d -> d.getId().equals(idDetalle))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Detalle con ID: {} no encontrado", idDetalle);
                    return new IllegalArgumentException("Detalle no encontrado con ID: " + idDetalle);
                });

        detalle.bloquear();
        repository.save(horario);
        log.info("Slot bloqueado correctamente");
    }

    /**
     * Desbloquea un slot
     *
     * @throws IllegalArgumentException Si:
     * <ul>
     *     <li>No existe un horario en la base de datos con el ID brindado</li>
     *     <li>No existe un detalle/slot en la base de datos con el ID brindado</li>
     * </ul>
     */
    @Transactional
    public void desbloquearSlot(Long idHorario, Long idDetalle) {
        log.info("Inicio de proceso de desbloquear slot: {}", idDetalle);

        Horario horario = repository.findById(idHorario)
                .orElseThrow(() -> {
                    log.warn("Horario con ID: {} no encontrado", idHorario);
                    return new IllegalArgumentException("Horario no encontrado con ID: " + idHorario);
                });

        DetalleHorario detalle = horario.getDetalles().stream()
                .filter(d -> d.getId().equals(idDetalle))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Detalle con ID: {} no encontrado", idDetalle);
                    return new IllegalArgumentException("Detalle no encontrado con ID: " + idDetalle);
                });

        detalle.desbloquear();
        repository.save(horario);
        log.info("Slot desbloqueado correctamente");
    }

    // VALIDACIONES

    /**
     * Valida que no hayan horarios cruzados para un médico
     *
     * @param idMedico Identificador único del médico
     * @param fecha Fecha del horario
     * @param horaInicio Hora de inicio del horario
     * @param horaFin Hora de fin del horario
     * @throws IllegalArgumentException Si la validación no se cumple
     */
    public void existeSolapamiento(
            Long idMedico,
            String nroConsultorio,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin
    ) {
        boolean existeSolapamiento = repository.existeSolapamiento(
                idMedico,
                nroConsultorio,
                fecha,
                horaInicio,
                horaFin);

        if (existeSolapamiento) {
            log.warn(
                    "Ya existe un horario en la fecha: {}, en el consultorio: {} para médico: {} en ese rango de horas: {} - {}",
                    fecha, nroConsultorio, idMedico, horaInicio, horaFin
            );
            throw new IllegalArgumentException(
                    "Ya existe un horario en ese rango de horas en el mismo consultorio"
            );
        }
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
     * si la especialidad no existe (404) devuelve null
     *
     * @param id Identificador único de la especialidad
     * @return Un objeto {@link EspecialidadResponse} que contiene el ID y nombre de la especialidad
     */
    private EspecialidadResponse obtenerEspecialidadNombre(Long id) {
        if (id == null || id <= 0) {
            log.error("Intento de obtener nombre con ID inválido: {}", id);
            throw new IllegalArgumentException("Id inválido");
        }

        return especialidadClient.obtenerEspecialidad(id);
    }

    /**
     * Obtiene el nro_consultorio y ubicación de un consultorio a
     * través del cliente {@code consultorioClient}
     *
     * @param nro_consultorio Número único del consultorio
     * @return Objeto {@link ConsultorioResponse} que contiene los datos del consultorio
     */
    private ConsultorioResponse obtenerConsultorio(String nro_consultorio) {
        if (nro_consultorio == null || nro_consultorio.isEmpty()) {
            log.error("Intento de obtener consultorio con N° inválido: {}", nro_consultorio);
            throw new IllegalArgumentException("Id inválido");
        }

        return consultorioClient.obtenerConsultorio(nro_consultorio.trim());
    }

    /**
     * Obtiene la Cita Médica de un Slot a través del cliente {@code citaMedicaClient}
     *
     * @param idCitaMedica Identificador único de la cita médica
     * @return Objeto {@link CitaMedicaFeignResponse} que contiene los datos de la cita médica
     */
    private CitaMedicaFeignResponse obtenerCitaMedica(Long idCitaMedica) {
        if (idCitaMedica == null || idCitaMedica <= 0) {
            log.error("Intento de obtener Cita Médica con ID inválido: {}", idCitaMedica);
            throw new IllegalArgumentException("Id inválido");
        }

        return citaMedicaClient.obtenerCita(idCitaMedica);
    }

    // SERVICIOS PARA BRINDAR DATOS A OTROS MICROSERVICIOS

    /**
     * Brinda los datos de un Slot específico
     *
     * @param idHorario Identificador único del Horario
     * @param idDetalle Identificador único del Slot del Horario
     * @return Objeto {@link SlotClientResponse} que contiene los datos del Slot
     */
    public SlotClientResponse brindarSlot(Long idHorario, Long idDetalle) {
        Horario horario = repository.findById(idHorario)
                .orElseThrow(() -> {
                    log.warn("Horario con ID: {} no encontrado", idHorario);
                    return new IllegalArgumentException("Horario no encontrado con ID: " + idHorario);
                });

        DetalleHorario detalle = horario.getDetalles().stream()
                .filter(d -> d.getId().equals(idDetalle))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Detalle con ID: {} no encontrado", idDetalle);
                    return new IllegalArgumentException("Detalle no encontrado con ID: " + idDetalle);
                });

        return toClientResponse(detalle, horario);
    }

    // MAPEADORES A DTO

    private HorarioResponse toResponse(Horario horario) {
        List<HorarioResponse.DetalleHorarioResponse> detallesResponse = horario.getDetalles()
                .stream()
                .map(this::toDetalleHorarioResponse)
                .toList();

        EmpleadoClientResponse empleado = obtenerEmpleadoNombre(horario.getIdEmpleado());
        ConsultorioResponse consultorio = obtenerConsultorio(horario.getNroConsultorio());
        EspecialidadResponse especialidad = obtenerEspecialidadNombre(horario.getIdEspecialidad());

        long slotsDisponibles = horario.contarSlotsDisponibles();

        long slotsOcupados = horario.contarSlotsOcupados();

        long slotsBloqueados = horario.contarSlotsBloqueados();

        return HorarioResponse.builder()
                .id(horario.getId())
                .empleado(empleado)
                .especialidad(especialidad)
                .consultorio(consultorio)
                .fecha(horario.getFecha())
                .horaInicio(horario.getHoraInicio())
                .horaFin(horario.getHoraFin())
                .detalles(detallesResponse)
                .totalSlots(horario.getDetalles().size())
                .slotsDisponibles(slotsDisponibles)
                .slotsOcupados(slotsOcupados)
                .slotsBloqueados(slotsBloqueados)
                .estaCompleto(horario.estaCompleto())
                .build();
    }

    private HorarioResponse.DetalleHorarioResponse toDetalleHorarioResponse(DetalleHorario detalle) {
        CitaMedicaFeignResponse cita = (detalle.getIdCita() == null)
                ? null
                : obtenerCitaMedica(detalle.getIdCita());

        return HorarioResponse.DetalleHorarioResponse.builder()
                .id(detalle.getId())
                .horaInicio(detalle.getHoraInicio())
                .horaFin(detalle.getHoraFin())
                .estado(detalle.getEstado())
                .cita(cita)
                .estaDisponible(detalle.estaDisponible())
                .build();
    }

    private SlotClientResponse toClientResponse(DetalleHorario detalle, Horario horario) {

        EmpleadoClientResponse medico = obtenerEmpleadoNombre(detalle.getIdEmpleado());
        EspecialidadResponse especialidad = obtenerEspecialidadNombre(horario.getIdEspecialidad());
        ConsultorioResponse consultorio = obtenerConsultorio(detalle.getNroConsultorio());
        LocalDate fecha = horario.getFecha();
        LocalTime hora = detalle.getHoraInicio();
        DetalleHorario.EstadoDetalleHorario estado = detalle.getEstado();

        return new SlotClientResponse(
                medico,
                especialidad,
                fecha,
                hora,
                consultorio,
                estado
        );
    }

}
