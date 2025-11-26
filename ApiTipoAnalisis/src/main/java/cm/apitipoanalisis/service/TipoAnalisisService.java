package cm.apitipoanalisis.service;

import cm.apitipoanalisis.dto.TipoAnalisisRequest;
import cm.apitipoanalisis.dto.TipoAnalisisResponse;
import cm.apitipoanalisis.repository.TipoAnalisis;
import cm.apitipoanalisis.repository.TipoAnalisisRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TipoAnalisisService {

    private final TipoAnalisisRepository repository;

    @Transactional
    public TipoAnalisisResponse registrar(TipoAnalisisRequest request) {
        log.info("Registrando Tipo Analisis: {}", request.nombre());

        log.debug("Validando existencia por nombre: {}", request.nombre());
        if (repository.existsByNombre(request.nombre())) {
            log.warn("Tipo Analisis con nombre: {} ya existe", request.nombre());
            throw new IllegalArgumentException("Tipo Analisis con nombre: " + request.nombre() + " ya existe");
        }
        log.debug("Validación completada");

        log.debug("Creando Tipo Analisis: {}", request);
        TipoAnalisis ta = TipoAnalisis.builder()
                .nombre(request.nombre())
                .muestraRequerida(request.muestraRequerida())
                .build();

        repository.save(ta);
        log.debug("Tipo Analisis creado correctamente: {}", ta);

        return toResponse(ta);
    }

    @Transactional(readOnly = true)
    public List<TipoAnalisisResponse> listar() {
        log.info("Listando Tipo Analisis");

        List<TipoAnalisis> tas = repository.findAll();
        log.info("Tipo Analisis listados correctamente: {}", tas.size());

        return tas.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TipoAnalisisResponse buscar(Long id) {
        log.info("Buscando Tipo Analisis: {}", id);

        TipoAnalisis ta = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tipo Analisis no encontrado: {}", id);
                    return new EntityNotFoundException("Tipo Analisis con id: " + id + " no existe");
                });
        log.info("Tipo Analisis encontrado correctamente");

        return toResponse(ta);
    }

    @Transactional
    public TipoAnalisisResponse actualizar(TipoAnalisisRequest request, Long id) {
        log.info("Actualizando Tipo Analisis: {}", request.nombre());

        TipoAnalisis ta = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tipo Analisis no encontrado: {}", id);
                    return new EntityNotFoundException("Tipo Analisis con id: " + id + " no existe");
                });

        if (!ta.getNombre().equals(request.nombre())) {
            log.debug("Validando nombre de Tipo Analisis: {}", request.nombre());
            if (repository.existsByNombreAndIdNot(request.nombre(), id)) {
                log.warn("Tipo Analisis con nombre: {} ya existe", request.nombre());
                throw new IllegalArgumentException("Tipo Analisis con nombre: " + request.nombre() + " ya existe");
            }
            log.debug("Validación completada");

            log.debug("Actualizando nombre de: {} a {}", ta.getNombre(), request.nombre());
            ta.setNombre(request.nombre());
        }

        if (!ta.getMuestraRequerida().equals(request.muestraRequerida())) {
            log.debug("Actualizando muestra de: {} a {}", ta.getMuestraRequerida(), request.muestraRequerida());
            ta.setMuestraRequerida(request.muestraRequerida());
        }

        repository.save(ta);
        log.debug("Tipo Analisis actualizada correctamente");

        return toResponse(ta);
    }

    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando Tipo Analisis: {}", id);

        if (!repository.existsById(id)) {
            log.warn("Tipo Analisis no encontrado: {}", id);
            throw new EntityNotFoundException("Tipo Análisis con ID: " + id + " no encontrado");
        }

        repository.deleteById(id);
        log.debug("Tipo Analisis eliminado correctamente: {}", id);
    }

    private TipoAnalisisResponse toResponse(TipoAnalisis ta) {
        return new TipoAnalisisResponse(
                ta.getId(),
                ta.getNombre(),
                ta.getMuestraRequerida()
        );
    }

}
