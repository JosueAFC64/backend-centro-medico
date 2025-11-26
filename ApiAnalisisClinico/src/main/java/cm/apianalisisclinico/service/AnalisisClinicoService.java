package cm.apianalisisclinico.service;

import cm.apianalisisclinico.client.empleado.EmpleadoClientResponse;
import cm.apianalisisclinico.client.empleado.EmpleadoFeignClient;
import cm.apianalisisclinico.client.paciente.PacienteFeignClient;
import cm.apianalisisclinico.client.paciente.PacienteSimpleResponse;
import cm.apianalisisclinico.client.tipoanalisis.TipoAnalisisFeignClient;
import cm.apianalisisclinico.client.tipoanalisis.TipoAnalisisResponse;
import cm.apianalisisclinico.dto.analisisclinico.AnalisisClinicoRequest;
import cm.apianalisisclinico.dto.analisisclinico.AnalisisClinicoResponse;
import cm.apianalisisclinico.dto.detalleanalisis.DetalleAnalisisResponse;
import cm.apianalisisclinico.repository.AnalisisClinico;
import cm.apianalisisclinico.repository.AnalisisClinicoRepository;
import cm.apianalisisclinico.repository.DetalleAnalisis;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalisisClinicoService {

    private final AnalisisClinicoRepository repository;
    private final AnalisisClinicoPdfService pdfService;
    private final TipoAnalisisFeignClient tipoAnalisisClient;
    public final PacienteFeignClient pacienteClient;
    public final EmpleadoFeignClient empleadoClient;

    @Transactional
    public byte[] registrar (AnalisisClinicoRequest request) {
        log.info("Registrando analisis clinico");

        log.debug("Creando entidad AnalisisClinico");
        AnalisisClinico ac = AnalisisClinico.builder()
                .fechaSolicitud(request.fechaSolicitud())
                .dniPaciente(request.dniPaciente())
                .idMedico(request.idMedico())
                .build();

        log.debug("Agregando Detalle Análisis a Análisis clínico");
        ac.agregarDetalles(request.tipoAnalisisIds());

        repository.save(ac);
        log.info("Análisis clínico registrado con éxito");

        return pdfService.generarOrdenAnalisis(toResponse(ac));
    }

    @Transactional(readOnly = true)
    public AnalisisClinicoResponse buscar(Long id) {
        log.info("Buscando analisis clinico: {}", id);

        AnalisisClinico ac = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Análisis Clínico con ID: {} no encontrado", id);
                    return new EntityNotFoundException("Análisis Clínico con ID: " + id + " no encontrado");
                });
        log.debug("Análisis Clínico con ID: {} encontrado", id);

        return toResponse(ac);
    }

    // SERVICIOS PARA OBTENER DATOS DE OTROS MICROSERVICIOS

    public TipoAnalisisResponse obtenerTipoAnalisis(Long idTipoAnalisis) {
        log.info("Obteniendo tipo Analisis");
        return tipoAnalisisClient.buscar(idTipoAnalisis);
    }

    public PacienteSimpleResponse obtenerPaciente(String dniPaciente) {
        log.info("Obteniendo Paciente");
        return pacienteClient.obtenerPacienteSimple(dniPaciente);
    }

    public EmpleadoClientResponse obtenerMedico(Long idMedico) {
        log.info("Obteniendo Médico");
        return empleadoClient.obtenerNombre(idMedico);
    }

    // MAPEADORES A DTO

    private AnalisisClinicoResponse toResponse(AnalisisClinico ac) {
        List<DetalleAnalisisResponse> detalles = ac.getDetalles()
                .stream()
                .map(this::toDetalleResponse)
                .toList();

        PacienteSimpleResponse paciente = obtenerPaciente(ac.getDniPaciente());
        EmpleadoClientResponse medico = obtenerMedico(ac.getIdMedico());

        return new AnalisisClinicoResponse(
                ac.getId(),
                ac.getFechaSolicitud(),
                paciente,
                medico,
                detalles
        );
    }

    private DetalleAnalisisResponse toDetalleResponse(DetalleAnalisis da) {
        TipoAnalisisResponse tipoAnalisis = obtenerTipoAnalisis(da.getIdTipoAnalisis());

        return new DetalleAnalisisResponse(
                da.getId(),
                tipoAnalisis
        );
    }

}
