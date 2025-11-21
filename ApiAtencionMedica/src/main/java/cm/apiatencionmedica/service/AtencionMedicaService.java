package cm.apiatencionmedica.service;

import cm.apiatencionmedica.client.historiamedica.HistoriaMedicaFeignClient;
import cm.apiatencionmedica.client.citamedica.CitaMedicaFeignClient;
import cm.apiatencionmedica.client.citamedica.CitaMedicaFeignResponse;
import cm.apiatencionmedica.dto.AtencionMedicaFeignResponse;
import cm.apiatencionmedica.dto.AtencionMedicaRequest;
import cm.apiatencionmedica.dto.AtencionMedicaResponse;
import cm.apiatencionmedica.repository.AtencionMedica;
import cm.apiatencionmedica.repository.AtencionMedicaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AtencionMedicaService {

    private final AtencionMedicaRepository repository;
    private final CitaMedicaFeignClient citaMedicaClient;
    private final HistoriaMedicaFeignClient historiaMedicaClient;

    @Transactional
    public AtencionMedicaResponse registrar(AtencionMedicaRequest request) {
        log.info("Inicio de proceso de registro para ID: {}", request.idCita());

        log.debug("Creando entidad AtencionMedica: {}", request);
        AtencionMedica atencionMedica = AtencionMedica.builder()
                .idCita(request.idCita())
                .diagnostico(request.diagnostico())
                .tratamiento(request.tratamiento())
                .observaciones(request.observaciones())
                .build();

        repository.save(atencionMedica);
        log.debug("Atención Médica registrada correctamente: {}", atencionMedica.getId());

        registrarAtencionMedica(atencionMedica);
        log.debug("Atención Médica añadida a la Historia Médica del Paciente correctamente");

        completarCita(atencionMedica.getIdCita());
        log.debug("Cita Médica marcada como COMPLETADA correctamente");

        return toResponse(atencionMedica);
    }

    @Transactional(readOnly = true)
    public AtencionMedicaResponse buscarPorIdCita(Long idCita) {
        AtencionMedica a = repository.findByIdCita(idCita)
                .orElseThrow(() -> {
                    log.warn("Atención Médica con ID Cita: {} no encontrada", idCita);
                    return new IllegalArgumentException("Atención Médica con ID Cita: " + idCita + " no encontrada");
                });

        return toResponse(a);
    }

    // SERVICIOS PARA OBTENER DATOS DE OTROS MICROSERVICIOS

    private CitaMedicaFeignResponse obtenerCitaMedica(Long idCita) {
        if (idCita == null || idCita <= 0) {
            log.warn("Intento de obtener cita médica con ID inválido: {}", idCita);
            throw new IllegalArgumentException("ID inválido");
        }

        return citaMedicaClient.obtenerCita(idCita);
    }

    // SERVICIOS PARA BRINDAR DATOS A OTROS MICROSERVICIOS

    public AtencionMedicaFeignResponse brindarAtencionMedica(Long id) {
        AtencionMedica a = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Atención Médica con ID: {} no encontrada", id);
                    return new IllegalArgumentException("Atención Médica con ID: " + id + " no encontrada");
                });

        return toFeignResponse(a);
    }

    public void completarCita(Long idCita) {
        if (idCita == null || idCita <= 0) {
            log.warn("Intento de obtener cita médica con ID inválido: {}", idCita);
            throw new IllegalArgumentException("ID inválido");
        }

        citaMedicaClient.completarCita(idCita);
    }

    public void registrarAtencionMedica(AtencionMedica a) {
        CitaMedicaFeignResponse cita = obtenerCitaMedica(a.getIdCita());

        try {
            historiaMedicaClient.registrarAtencionMedica(cita.paciente().dni(), a.getId());
            log.info("Atención Médica añadida a la Historia Médica correctamente");
        } catch (Exception e) {
            repository.deleteById(a.getId());
            throw new RuntimeException("Error inesperado al registrar atención en historia, eliminando atención", e);
        }
    }

    // MAPEADORES A DTO

    private AtencionMedicaResponse toResponse(AtencionMedica a) {
        return new AtencionMedicaResponse(
                a.getId(),
                a.getDiagnostico(),
                a.getTratamiento(),
                a.getObservaciones()
        );
    }

    private AtencionMedicaFeignResponse toFeignResponse(AtencionMedica a) {
        CitaMedicaFeignResponse cita = obtenerCitaMedica(a.getIdCita());

        return new AtencionMedicaFeignResponse(
                a.getId(),
                a.getDiagnostico(),
                a.getTratamiento(),
                a.getObservaciones(),
                cita
        );
    }
}
