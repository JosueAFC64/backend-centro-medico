package cm.apiconsultorio.service;

import cm.apiconsultorio.dto.ConsultorioRequest;
import cm.apiconsultorio.dto.ConsultorioResponse;
import cm.apiconsultorio.repository.Consultorio;
import cm.apiconsultorio.repository.ConsultorioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultorioService {

    private final ConsultorioRepository repository;

    // SERVICIOS CRUD

    /**
     * Registra un consultorio en la base de datos
     *
     * @param request Objeto {@link ConsultorioRequest} que contiene los datos del consultorio
     * @return Objeto {@link ConsultorioResponse} que contiene los datos del consultorio ya registrado
     */
    @Transactional
    public ConsultorioResponse registrar(ConsultorioRequest request) {
        log.info("Inicio proceso de registro para: {}", request.nro_consultorio());

        log.debug("Realizando validaciones");
        validarConsultorio(null, request.nro_consultorio());
        log.debug("Validaciones sin excepciones encontradas");

        log.debug("Creando entidad consultorio");
        Consultorio c = Consultorio.builder()
                .nroConsultorio(request.nro_consultorio().trim())
                .ubicacion(request.ubicacion().trim())
                .build();

        repository.save(c);
        log.info("Consultorio registrado correctamente con ID: {}", c.getId());

        return toResponse(c);
    }

    /**
     * Lista todos los consultorios de la base de datos
     *
     * @return Lista de objetos {@link ConsultorioResponse} que contienen los datos de los consultorios
     */
    @Transactional(readOnly = true)
    public List<ConsultorioResponse> listar() {
        log.info("Inicio proceso de listar");

        List<Consultorio> consultorios = repository.findAll();

        log.info("Consultorios listados correctamente: {}", consultorios.size());

        return consultorios.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Actualiza los datos de un consultorio
     *
     * @param request Objeto {@link ConsultorioRequest} que contiene los datos actualizados
     * @param id Identificador único del consultorio
     * @return Objeto {@link ConsultorioResponse} que contiene los datos ya actualizados
     * @throws EntityNotFoundException Si no se encuentra un consultorio con el ID brindado
     */
    @Transactional
    public ConsultorioResponse actualizar(ConsultorioRequest request, Long id) {

        log.info("Inicio proceso de actualizar para ID: {}", id);

        Consultorio c = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Consultorio con ID: {} no encontrado", id);
                    return new EntityNotFoundException("Consultorio con ID: " + id + " no encontrado");
                });
        log.debug("Consultorio con ID: {} encontrada", id);

        log.debug("Realizando validaciones");
        validarConsultorio(id, request.nro_consultorio().trim());
        log.debug("Validaciones sin excepciones encontradas");

        if (!c.getNroConsultorio().equals(request.nro_consultorio().trim())) {
            log.debug("Actualizando N° de consultorio de {} a {}", c.getNroConsultorio(), request.nro_consultorio());
            c.setNroConsultorio(request.nro_consultorio().trim());
        }

        if (!c.getUbicacion().equals(request.ubicacion().trim())) {
            log.debug("Actualizando ubicación de {} a {}", c.getUbicacion(), request.ubicacion());
            c.setUbicacion(request.ubicacion().trim());
        }

        repository.save(c);
        log.info("Consultorio con ID: {} actualizado", id);

        return toResponse(c);
    }

    /**
     * Elimina un consultorio de la base de datos (permanente)
     *
     * @param nro_consultorio Número único del consultorio
     * @throws EntityNotFoundException Si no se encuentra un consultorio con el ID brindado
     */
    @Transactional
    public void eliminar(String nro_consultorio) {
        log.info("Inicio de proceso de eliminación para N° Consultorio: {}", nro_consultorio);

        if (!repository.existsByNroConsultorio(nro_consultorio)) {
            log.warn("Consultorio con N°: {} no encontrado", nro_consultorio);
            throw new EntityNotFoundException("Consultorio con N°: " + nro_consultorio + " no encontrado");
        }

        repository.deleteByNroConsultorio(nro_consultorio);
        log.info("Consultorio con N°: {} eliminado", nro_consultorio);
    }

    // SERVICIOS PARA BRINDAR DATOS A OTROS MICROSERVICIOS

    /**
     * Busca un consultorio específico por ID
     *
     * @param nro_consultorio Número único del consultorio
     * @return Objeto {@link ConsultorioResponse} que contiene los datos del consultorio
     */
    @Transactional(readOnly = true)
    public ConsultorioResponse brindarConsultorio(String nro_consultorio) {
        log.info("Inicio proceso de buscar para N°: {}", nro_consultorio);

        Consultorio c = repository.findByNroConsultorio(nro_consultorio)
                .orElseThrow(() -> {
                    log.warn("Consultorio con N°: {} no encontrado", nro_consultorio);
                    return new EntityNotFoundException("Consultorio con N°: " + nro_consultorio + " no encontrado");
                });

        log.info("Consultorio con N°: {} encontrado", nro_consultorio);

        return toResponse(c);
    }

    // VALIDACIONES

    /**
     * Realiza las siguientes validaciones
     * <ul>
     *     <li>
     *         Para registrar: Que no exista otro consultorio con el mismo {@code nro_consultorio}
     *     </li>
     *     <li>
     *         Para actualizar: Que no exista otro consultorio aparte del mismo
     *         con el mismo {@code nro_consultorio}
     *     </li>
     * </ul>
     *
     * @param id Identificador único del consultorio
     * @param nro_consultorio Código/Número único del consultorio
     */
    public void validarConsultorio(Long id, String nro_consultorio) {
        boolean yaExiste = (id == null)
                ? repository.existsByNroConsultorio(nro_consultorio)
                : repository.existsByNroConsultorioAndIdNot(nro_consultorio, id);

        if (yaExiste) {
            throw new IllegalArgumentException(
                    "Ya existe un consultorio registrado con el número: " + nro_consultorio
            );
        }
    }

    // MAPEADORES A DTO

    public ConsultorioResponse toResponse (Consultorio c) {
        return new ConsultorioResponse(
                c.getNroConsultorio(),
                c.getUbicacion()
        );
    }

}
