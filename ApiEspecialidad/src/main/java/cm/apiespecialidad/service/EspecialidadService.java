package cm.apiespecialidad.service;

import cm.apiespecialidad.dto.EspecialidadRequest;
import cm.apiespecialidad.dto.EspecialidadResponse;
import cm.apiespecialidad.repository.Especialidad;
import cm.apiespecialidad.repository.EspecialidadRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EspecialidadService {

    private final EspecialidadRepository repository;

    // SERVICIOS CRUD

    /**
     * Registra una nueva especialidad en la base de datos
     *
     * @param request Objeto {@link EspecialidadRequest} que contiene el nombre requerido para
     *                registrar la especialidad
     * @return Un objeto {@link EspecialidadResponse} con los datos de la especialidad registrada
     * @throws IllegalArgumentException si:
     * <ul>
     *     <li>El objeto request es nulo</li>
     *     <li>Ya existe una especialidad con el mismo nombre</li>
     * </ul>
     */
    @Transactional
    public EspecialidadResponse registrar(EspecialidadRequest request) {
        if (request == null) {
            log.error("Intento de registro con request nulo");
            throw new IllegalArgumentException("Request invalido");
        }

        log.info("Inicio de proceso de registro: {}", request.nombre());

        log.debug("Validando existencia por nombre: {}", request.nombre());
        if (repository.existsByNombreContainingIgnoreCase(request.nombre())){
            log.info("Intento de registro con nombre duplicado: {}", request.nombre());
            throw new IllegalArgumentException("Ya existe una especialidad con nombre: " + request.nombre());
        }

        log.debug("Creando entidad Especialidad");
        Especialidad e = Especialidad.builder()
                .nombre(request.nombre().trim())
                .costo(request.costo())
                .build();

        repository.save(e);
        log.info("Especialidad registrada correctamente con ID: {}", e.getId());

        return toResponse(e);
    }

    /**
     * Lista todas las especialidades de la base de datos
     *
     * @return Una lista de objetos {@link EspecialidadResponse} que contiene:
     * <ul>
     *     <li>ID de la especialidad</li>
     *     <li>Nombre de la especialidad</li>
     * </ul>
     */
    @Transactional(readOnly = true)
    public List<EspecialidadResponse> listar(){

        log.info("Inicio de proceso de listar");

        List<Especialidad> especialidades = repository.findAll();

        log.info("Especialidades listadas correctamente: {}", especialidades.size());

        return especialidades
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Busca una especialidad específica por ID
     *
     * @param id Identificador único de la especialidad
     * @return Un objeto {@link EspecialidadResponse} que contiene:
     * <ul>
     *     <li>ID de la especialidad</li>
     *     <li>Nombre de la especialidad</li>
     * </ul>
     * @throws IllegalArgumentException si el ID es inválido
     * @throws EntityNotFoundException si no existe una especialidad en la base de datos con el ID brindado
     */
    @Transactional(readOnly = true)
    public EspecialidadResponse buscar(Long id) {
        if (id == null || id <= 0) {
            log.error("Intento de búsqueda con ID inválido: {}", id);
            throw new IllegalArgumentException("ID invalido");
        }

        log.info("Inicio de proceso de búsqueda con ID: {}", id);

        Optional<Especialidad> especialidad = repository.findById(id);

        return especialidad
                .map(e -> {
                    log.info("Especialidad con ID: {} encontrada correctamente", id);
                    return toResponse(e);
                })
                .orElseThrow(() -> {
                    log.warn("Especialidad con ID: {} no encontrada", id);
                    return new EntityNotFoundException("Especialidad con ID: " + id + " no encontrada");
                });
    }

    /**
     * Actualiza los datos de una especialidad
     *
     * @param request Objeto {@link EspecialidadRequest} que contiene el nombre nuevo de la especialidad
     * @param id Identificador único de la especialidad
     * @return Un objeto {@link EspecialidadResponse} que contiene:
     * <ul>
     *     <li>ID de la especialidad</li>
     *     <li>Nombre actualizado de la especialidad</li>
     * </ul>
     * @throws EntityNotFoundException Si no existe una especialidad en la base de datos con el ID brindado
     * @throws IllegalArgumentException Si:
     * <ul>
     *     <li>El ID es inválido</li>
     *     <li>El objeto request es nulo</li>
     *     <li>Ya existe una especialidad con el mismo nombre</li>
     * </ul>
     */
    @Transactional
    public EspecialidadResponse actualizar(EspecialidadRequest request, Long id) {
        if (id == null || id <= 0) {
            log.error("Intento de actualización con ID inválido: {}", id);
            throw new IllegalArgumentException("Id invalido");
        }

        if (request == null){
            log.error("Intento de actualización con request nulo para ID: {}", id);
            throw new IllegalArgumentException("Request invalido");
        }

        log.info("Inicio de proceso de actualización con ID: {}", id);

        Especialidad especialidad = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Especialidad con ID: {} no encontrada", id);
                    return new EntityNotFoundException("Especialidad con ID " + id + " no encontrada");
                });

        log.debug("Especialidad encontrada: {}", especialidad.getNombre());

        String nuevoNombre = request.nombre().trim();

        if (!nuevoNombre.equals(especialidad.getNombre())){
            log.debug("Verificando duplicado de nombre: {}", request.nombre());
            if (repository.existsByNombreAndIdNot(nuevoNombre, id)){
                log.warn("Intento de actualización con nombre duplicado: {}", request.nombre());
                throw new IllegalArgumentException("Ya existe una especialidad con el nombre: " + nuevoNombre);
            }
            especialidad.setNombre(nuevoNombre);
        }

        if (!request.costo().equals(especialidad.getCosto())){
            log.info("Actualizando Costo de {} a {}", especialidad.getCosto(), request.costo());
            especialidad.setCosto(request.costo());
        }

        repository.save(especialidad);
        log.info("Especialidad con ID: {} actualizada correctamente", especialidad.getId());

        return toResponse(especialidad);
    }

    /**
     * Elimina una especialidad de la base de datos
     *
     * @param id Identificador único de la especialidad
     */
    @Transactional
    public void eliminar(Long id){
        log.info("Inicio de proceso de eliminación con ID: {}", id);

        Especialidad e = repository.findById(id)
                        .orElseThrow(() -> {
                            log.warn("Especialidad con ID: {} no encontrada", id);
                            return new EntityNotFoundException("Especialidad con ID: " + id + " no encontrada");
                        });

        repository.delete(e);

        log.info("Especialidad con ID: {} eliminado correctamente", id);
    }

    // MAPEADORES A DTO

    private EspecialidadResponse toResponse(Especialidad especialidad) {
        return new EspecialidadResponse(
                especialidad.getId(),
                especialidad.getNombre(),
                especialidad.getCosto()
        );
    }

}
