package cm.apiempleado.service;

import cm.apiempleado.client.especialidad.EspecialidadFeignClient;
import cm.apiempleado.client.especialidad.EspecialidadResponse;
import cm.apiempleado.client.usuario.UserFeignClient;
import cm.apiempleado.client.usuario.UserRequest;
import cm.apiempleado.dto.request.EmpleadoRequest;
import cm.apiempleado.dto.response.*;
import cm.apiempleado.repository.EmpleadoRepository;
import cm.apiempleado.repository.Empleados;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmpleadoService {

    private final EspecialidadFeignClient especialidadClient;
    private final UserFeignClient userClient;
    private final EmpleadoRepository repository;

    // SERVICIOS CRUD

    /**
     * Registra un nuevo empleado en la base de datos
     *
     * @param request Objeto {@link EmpleadoRequest} que contiene los datos requeridos para registrar al empleado
     * @return Un objeto {@link EmpleadoResponse} que contiene los datos del empleado
     * @throws IllegalArgumentException Si:
     * <ul>
     *     <li>Existe un empleado con el mismo DNI</li>
     *     <li>Existe un empleado con el mismo teléfono</li>
     *     <li>Existe un empleado con el mismo correo</li>
     * </ul>
     */
    @Transactional
    public EmpleadoResponse registrar(EmpleadoRequest request){
        log.info("Inicio de proceso de registro: {}", request.correo());

        log.debug("Validando existencia por DNI: {}", request.dni());
        if (repository.existsByDni(request.dni())){
            log.info("Intento de registro con DNI duplicado: {}", request.dni());
            throw new IllegalArgumentException("Ya existe un empleado con el DNI: " + request.dni());
        }

        log.debug("Validando existencia por teléfono: {}", request.telefono());
        if (repository.existsByTelefono(request.telefono())){
            log.info("Intento de registro con teléfono duplicado: {}", request.telefono());
            throw new IllegalArgumentException("Ya existe un empleado con el teléfono: " + request.telefono());
        }

        log.debug("Validando existencia por correo: {}", request.correo());
        if (repository.existsByCorreo(request.correo())){
            log.info("Intento de registro con correo duplicado: {}", request.correo());
            throw new IllegalArgumentException("Ya existe un empleado con el correo: " + request.correo());
        }

        log.debug("Creando entidad Empleado");
        Empleados e = Empleados.builder()
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .cargo(request.cargo())
                .dni(request.dni())
                .telefono(request.telefono())
                .correo(request.correo())
                .fechaIngreso(request.fechaIngreso())
                .activo(request.activo())
                .especialidadIds(new HashSet<>(request.especialidadesIds()))
                .build();
        log.debug("Registrando usuario para el Empleado");
//        registrarUsuario(e.getNombres(), e.getCorreo(), e.getDni(), e.getCargo().toString());
        log.info("Usuario registrado correctamente");

        repository.save(e);
        log.info("Empleado registrado correctamente con ID: {}", e.getId());

        return toResponse(e);
    }

    /**
     * Lista a todos los empleados activos de la base de datos
     *
     * @return Una lista de objetos {@link EmpleadoSumResponse} que contiene:
     * <ul>
     *     <li>ID del empleado</li>
     *     <li>Nombre completo del empleado</li>
     *     <li>Cargo del empleado</li>
     *     <li>Nombres de las especialidades asociadas al empleado</li>
     * </ul>
     */
    @Transactional(readOnly = true)
    public List<EmpleadoSumResponse> listar(){
        log.info("Inicio de proceso de listar");

        List<Empleados> empleados = repository.findAll();

        log.info("Empleados listados correctamente: {}", empleados.size());

        return empleados
                .stream()
                .filter(Empleados::getActivo)
                .map(this::toSumResponse)
                .toList();
    }

    /**
     * Busca a un empleado en específico por ID
     *
     * @param id Identificador único del empleado
     * @return Un objeto {@link EmpleadoResponse} que contiene los datos del empleado
     * @throws EntityNotFoundException Si no existe un empleado en la base de datos con el ID brindado
     */
    @Transactional(readOnly = true)
    public EmpleadoResponse buscar(Long id){
        log.info("Inicio de proceso de búsqueda con ID: {}", id);

        Empleados empleado = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Empleado con ID: {} no encontrado", id);
                    return new EntityNotFoundException("Empleado con ID: " + id + "no encontrado");
                });

        log.info("Empleado con ID: {} encontrado", id);

        return toResponse(empleado);
    }

    /**
     * Actualiza los datos de un empleado
     *
     * @param request Objeto {@link EmpleadoRequest} que contiene los datos disponibles para actualizar
     * @param id Identificador único del empleado
     * @return Un objeto {@link EmpleadoResponse} que contiene los datos actualizados del empleado
     * @throws EntityNotFoundException Si no existe un empleado en la base de datos con el ID brindado
     * @throws IllegalArgumentException Si:
     * <ul>
     *     <li>Ya existe un empleado con el mismo DNI</li>
     *     <li>Ya existe un empleado con el mismo teléfono</li>
     *     <li>Ya existe un empleado con el mismo correo</li>
     * </ul>
     */
    @Transactional
    public EmpleadoResponse actualizar(EmpleadoRequest request, Long id) {
        log.info("Inicio de proceso de actualización con ID: {}", id);

        Empleados e = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Empleado con ID: {} no encontrado", id);
                    return new EntityNotFoundException("Empleado con ID: " + id + " no encontrado");
                });

        log.debug("Empleado encontrado: {}", e.getCorreo());

        if (!request.nombres().equals(e.getNombres())) {
            log.debug("Actualizando nombre de '{}' a '{}'", e.getNombres(), request.nombres());
            e.setNombres(request.nombres());
        }

        if (!request.apellidos().equals(e.getApellidos())) {
            log.debug("Actualizando apellidos de '{}' a '{}'", e.getApellidos(), request.apellidos());
            e.setApellidos(request.apellidos());
        }

        if (!request.cargo().equals(e.getCargo())) {
            log.debug("Actualizando cargo de '{}' a '{}'", e.getCargo(), request.cargo());
            e.setCargo(request.cargo());
        }

        if (!request.fechaIngreso().equals(e.getFechaIngreso())) {
            log.debug("Actualizando fecha de ingreso de '{}' a '{}", e.getFechaIngreso(), request.fechaIngreso());
            e.setFechaIngreso(request.fechaIngreso());
        }

        if (!request.activo().equals(e.getActivo())) {
            log.debug("Actualizando estado de '{}' a '{}'", e.getActivo(), request.activo());
            e.setActivo(request.activo());
        }

        log.debug("Actualizando especialidades del empleado ID: {}", id);
        e.setEspecialidadIds(new HashSet<>(request.especialidadesIds()));

        if (!request.dni().equals(e.getDni())){
            log.debug("Verificando duplicado de DNI: {}", request.dni());
            if (repository.existsByDniAndIdNot(request.dni(), e.getId())){
                log.info("Intento de actualización con DNI duplicado: {}", request.dni());
                throw new IllegalArgumentException("Ya hay un empleado registrado con el DNI: " + request.dni());
            }
            e.setDni(request.dni());
        }

        if (!request.telefono().equals(e.getTelefono())){
            log.debug("Verificando duplicado de teléfono: {}", request.telefono());
            if (repository.existsByTelefonoAndIdNot(request.telefono(), e.getId())){
                log.info("Intento de actualización con teléfono duplicado: {}", request.telefono());
                throw new IllegalArgumentException("Ya hay un empleado registrado con el teléfono: " + request.telefono());
            }
            e.setTelefono(request.telefono());
        }

        if (!request.correo().equals(e.getCorreo())){
            log.debug("Verificando duplicado de correo: {}", request.correo());
            if (repository.existsByCorreoAndIdNot(request.correo(), e.getId())){
                log.info("Intento de actualización con correo duplicado: {}", request.correo());
                throw new IllegalArgumentException("Ya hay un empleado registrado con el correo: " + request.correo());
            }
            e.setCorreo(request.correo());
        }

        repository.save(e);
        log.info("Empleado con ID: {} actualizado correctamente", id);

        return toResponse(e);
    }

    /**
     * Elimina un empleado de la base de datos
     *
     * @param id Identificador único del empleado
     * @throws IllegalArgumentException Si el ID es inválido
     * @throws EntityNotFoundException Si no existe un empleado en la base de datos con el ID brindado
     */
    @Transactional
    public void eliminar(Long id) {
        log.info("Inicio de proceso de eliminación con ID: {}", id);

        Empleados e = repository.findById(id)
                        .orElseThrow(() -> {
                            log.warn("Empleado con ID: {} no encontrado", id);
                            return new EntityNotFoundException("Empleado con ID: " + id + " no encontrado");
                        });

        repository.delete(e);

        log.info("Empleado con ID: {} eliminado correctamente", id);
    }

    // OTROS SERVICIOS

    /**
     * Convierte una lista de objetos {@link Empleados} y un ID de especialidad en una respuesta
     * {@link MedicoPorEspecialidadResponse} que contiene el nombre de la especialidad y los datos
     * de los médicos asociados.
     *
     * @param especialidadId Identificador único de la especialidad.
     * @return Un objeto {@link MedicoPorEspecialidadResponse} que contiene:
     * <ul>
     *     <li>Nombre de la especialidad</li>
     *     <li>Lista de: ID y nombre completo de cada médico</li>
     * </ul>
     * @throws IllegalArgumentException Si el ID es inválido.
     */
    @Transactional(readOnly = true)
    public MedicoPorEspecialidadResponse filtrarPorEspecialidad(Long especialidadId){
        log.info("Inicio de proceso de filtrar por especialidad con ID: {}", especialidadId);

        List<Empleados> medicos = repository
                .findByCargoAndEspecialidadIdsContaining(Empleados.Cargos.MEDICO, especialidadId)
                .stream()
                .filter(Empleados::getActivo)
                .toList();

        log.info("Médicos filtrados correctamente por especialidad con ID: {}", especialidadId);

        return toMedicoPorEspecialidadResponse(medicos, especialidadId);
    }

    @Transactional(readOnly = true)
    public List<EmpleadoClientResponse> listarMedicos() {

        List<Empleados> medicos = repository.findAll();

        return medicos.stream()
                .filter(m -> m.getCargo() == Empleados.Cargos.MEDICO)
                .map(this::toClientResponse)
                .toList();
    }

    // SERVICIOS PARA BRINDAR DATOS A OTROS MICROSERVICIOS

    /**
     * Busca un {@link Empleados} y devuelve su ID y nombre completo
     *
     * @param id Identificador único del empleado
     * @return Un objeto {@link EmpleadoClientResponse} con el ID y nombre completo del empleado
     * @throws EntityNotFoundException Si no existe un empleado en la base de datos con el ID brindado
     */
    @Transactional(readOnly = true)
    public EmpleadoClientResponse brindarNombre(Long id) {
        log.info("Inicio de proceso de búsqueda con ID: {}", id);
         Optional<Empleados> empleado = repository.findById(id);

        return empleado
                .map(e -> {
                    log.info("Empleado con ID: {} encontrado", id);
                    return toClientResponse(e);
                })
                .orElseThrow(() -> {
                    log.warn("Empleado con ID: {} no encontrado", id);
                    return new EntityNotFoundException("Empleado con ID: " + id + " no encontrado");
                });
    }

    public void registrarUsuario(String nombre, String email, String password, String rol) {
        try {
            UserRequest request = new UserRequest(
                    nombre,
                    email,
                    password,
                    rol
            );

            userClient.registrar(request);
        } catch (Exception e) {
            log.error("Error al registrar Usuario: {}", e.getMessage());
            throw new RuntimeException("Error inesperado al registrar usuario", e);
        }

    }

    // SERVICIOS PARA OBTENER DATOS DE OTROS MICROSERVICIOS

    /**
     * Obtiene el ID y nombre de una especialidad a través del cliente {@code especialidadClient},
     * si la especialidad no existe (404) devuelve null
     *
     * @param id Identificador único de la especialidad
     * @return Un objeto {@link EspecialidadResponse} que contiene el ID y nombre de la especialidad
     */
    private EspecialidadResponse obtenerEspecialidad(Long id){
        log.info("Nombre de especialidad con ID: {} encontrado correctamente", id);
        return especialidadClient.obtenerEspecialidad(id);
    }

    // MAPEADORES A DTO

    private EmpleadoResponse toResponse(Empleados empleado){
        List<EspecialidadResponse> especialidades = empleado.getEspecialidadIds() == null || empleado.getEspecialidadIds().isEmpty()
                ? List.of()
                : empleado.getEspecialidadIds()
                .stream()
                .map(id -> {
                    EspecialidadResponse esp = obtenerEspecialidad(id);
                    if (esp == null) {
                        eliminarEspecialidadNoEncontrada(id, empleado);
                    }
                    return esp;
                })
                .filter(Objects::nonNull)
                .toList();

        return new EmpleadoResponse(
                empleado.getId(),
                empleado.getNombres(),
                empleado.getApellidos(),
                empleado.getCargo(),
                empleado.getDni(),
                empleado.getTelefono(),
                empleado.getCorreo(),
                empleado.getFechaIngreso(),
                empleado.getActivo(),
                especialidades
        );
    }

    private EmpleadoSumResponse toSumResponse(Empleados empleado) {
        List<EspecialidadResponse> especialidades = empleado.getEspecialidadIds() == null || empleado.getEspecialidadIds().isEmpty()
                ? List.of()
                : empleado.getEspecialidadIds()
                .stream()
                .map(id -> {
                    EspecialidadResponse esp = obtenerEspecialidad(id);
                    if (esp == null) {
                        eliminarEspecialidadNoEncontrada(id, empleado);
                    }
                    return esp;
                })
                .filter(Objects::nonNull)
                .toList();

        return new EmpleadoSumResponse(
                empleado.getId(),
                empleado.getNombres() + " " + empleado.getApellidos(),
                empleado.getCargo(),
                especialidades
        );
    }

    private MedicoPorEspecialidadResponse toMedicoPorEspecialidadResponse(List<Empleados> medicos, Long especialidadId){
        EspecialidadResponse especialidad = Optional.ofNullable(obtenerEspecialidad(especialidadId))
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se pudo obtener la especialidad con ID: " + especialidadId)
                );

        List<MedicoPorEspecialidadResponse.DatosMedico> datosMedicos = medicos.stream()
                .map(this::toDatosMedico)
                .toList();

        return new MedicoPorEspecialidadResponse(
                especialidad,
                datosMedicos
        );
    }

    private MedicoPorEspecialidadResponse.DatosMedico toDatosMedico(Empleados medico){
        String nombreCompleto = medico.getNombres() + " " + medico.getApellidos();
        return new MedicoPorEspecialidadResponse.DatosMedico(
                medico.getId(),
                nombreCompleto
        );
    }

    private EmpleadoClientResponse toClientResponse(Empleados empleado) {
        return new EmpleadoClientResponse(
                empleado.getId(),
                empleado.getNombres() + " " + empleado.getApellidos()
        );
    }

    private void eliminarEspecialidadNoEncontrada(Long id, Empleados e) {
        if (e != null && e.getEspecialidadIds() != null) {
            e.getEspecialidadIds().remove(id);
        }
    }
}
