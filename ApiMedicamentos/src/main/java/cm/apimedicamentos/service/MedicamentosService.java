package cm.apimedicamentos.service;

import cm.apimedicamentos.dto.MedicamentosRequest;
import cm.apimedicamentos.dto.MedicamentosResponse;
import cm.apimedicamentos.repository.Medicamentos;
import cm.apimedicamentos.repository.MedicamentosRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicamentosService {

    private final MedicamentosRepository repository;

    @Transactional
    public MedicamentosResponse registrar(MedicamentosRequest request) {
        log.info("Registrando Medicamento: {}", request.nombre());

        log.debug("Validando duplicidad por nombre: {}", request.nombre());
        if (repository.existsByNombreContainingIgnoreCase(request.nombre())) {
            log.warn("Medicamento con nombre: {} ya existe", request.nombre());
            throw new IllegalArgumentException("Medicamento con nombre: " + request.nombre() + " ya existe");
        }
        log.debug("Validación completada");

        Medicamentos m = Medicamentos.builder()
                .nombre(request.nombre())
                .presentacion(request.presentacion())
                .build();

        repository.save(m);
        log.info("Medicamento registrado correctamente con ID: {}", m.getId());

        return toResponse(m);
    }

    @Transactional(readOnly = true)
    public List<MedicamentosResponse> listar() {
        log.info("Listando Medicamentos");

        List<Medicamentos> medicamentos = repository.findAll();

        log.info("Medicamentos listados correctamente: {}", medicamentos.size());

        return medicamentos.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MedicamentosResponse buscar(Long id) {
        log.info("Buscando Medicamento: {}", id);

        Medicamentos m = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Medicamento con ID: {} no encontrado", id);
                    return new EntityNotFoundException("Medicamento con ID: " + id + " no encontrado");
                });
        log.debug("Medicamento con ID: {} encontrado correctamente", id);

        return toResponse(m);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Medicamento con ID: {} no encontrado", id);
            throw new EntityNotFoundException("Medicamento con ID: " + id + " no encontrado");
        }

        repository.deleteById(id);
        log.info("Medicamento con ID: {} eliminado correctamente", id);
    }

    private MedicamentosResponse toResponse(Medicamentos m) {
        return new MedicamentosResponse(
                m.getId(),
                m.getNombre(),
                m.getPresentacion()
        );
    }

}
