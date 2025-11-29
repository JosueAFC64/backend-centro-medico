package cm.apirecetamedica.service;

import cm.apirecetamedica.client.empleado.EmpleadoClientResponse;
import cm.apirecetamedica.client.empleado.EmpleadoFeignClient;
import cm.apirecetamedica.client.medicamentos.MedicamentosFeignClient;
import cm.apirecetamedica.client.medicamentos.MedicamentosResponse;
import cm.apirecetamedica.client.paciente.PacienteFeignClient;
import cm.apirecetamedica.client.paciente.PacienteSimpleResponse;
import cm.apirecetamedica.dto.detallereceta.DetalleRecetaResponse;
import cm.apirecetamedica.dto.recetamedica.RecetaMedicaRequest;
import cm.apirecetamedica.dto.recetamedica.RecetaMedicaResponse;
import cm.apirecetamedica.repository.DetalleReceta;
import cm.apirecetamedica.repository.RecetaMedica;
import cm.apirecetamedica.repository.RecetaMedicaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecetaMedicaService {

    private final RecetaMedicaRepository repository;
    private final PacienteFeignClient pacienteClient;
    private final EmpleadoFeignClient empleadoClient;
    private final MedicamentosFeignClient medicamentosClient;
    private final RecetaMedicaPdfService pdfService;

    @Transactional
    public byte[] registrar(RecetaMedicaRequest request) {
        log.info("Registrando Receta Médica");

        log.debug("Creando entidad RecetaMedica");
        RecetaMedica rm = RecetaMedica.builder()
                .dniPaciente(request.dniPaciente())
                .idMedico(request.idMedico())
                .idAtencion(request.idAtencion())
                .fechaSolicitud(request.fechaSolicitud())
                .build();

        log.debug("Agregando DetalleReceta a Receta Médica");
        rm.agregarDetalles(request.detalles());

        repository.save(rm);
        log.info("Receta Médica registrada correctamente con ID: {}", rm.getId());

        return pdfService.generarPdfRecetaMedica(toResponse(rm));
    }

    @Transactional(readOnly = true)
    public RecetaMedicaResponse buscar(Long id) {
        log.info("Buscando Receta Médica por ID: {}", id);

        RecetaMedica rm = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Receta Médica con ID: {} no encontrada", id);
                    return new EntityNotFoundException("Receta Médica con ID: " + id + " no encontrada");
                });
        log.info("Receta Médica con ID: {} encontrada", id);

        return toResponse(rm);
    }

    // SERVICIOS PARA OBTENER DATOS DE OTROS MICROSERVICIOS

    public MedicamentosResponse obtenerMedicamento(Long idMedicamento) {
        log.info("Obteniendo Medicamento con ID: {}", idMedicamento);
        return medicamentosClient.obtenerMedicamento(idMedicamento);
    }

    public EmpleadoClientResponse obtenerMedico(Long idMedico) {
        log.info("Obteniendo Médico con ID: {}", idMedico);
        return empleadoClient.obtenerNombre(idMedico);
    }

    public PacienteSimpleResponse obtenerPaciente(String dniPaciente) {
        log.info("Obteniendo Paciente con DNI: {}", dniPaciente);
        return pacienteClient.obtenerPacienteSimple(dniPaciente);
    }

    // SERVICIOS PARA OBTENER DATOS DE OTROS MICROSERVICIOS

    @Transactional(readOnly = true)
    public RecetaMedicaResponse brindarReceta(Long idAtencion) {
        log.info("Brindado Receta Médica por ID Atención Médica: {}", idAtencion);

        RecetaMedica rm = repository.findByIdAtencion(idAtencion)
                .orElseThrow(() -> {
                    log.warn("Receta Médica con ID Atención Médica: {} no encontrada", idAtencion);
                    return new EntityNotFoundException("Receta Médica con ID Atención Médica: " + idAtencion + " no encontrada");
                });
        log.info("Receta Médica con ID Atención Médica: {} encontrada", idAtencion);

        return toResponse(rm);
    }

    // MAPEADORES A DTO

    private RecetaMedicaResponse toResponse(RecetaMedica rm) {
        List<DetalleRecetaResponse> detalles = rm.getDetalles()
                .stream()
                .map(this::toDetalleResponse)
                .toList();

        PacienteSimpleResponse paciente = obtenerPaciente(rm.getDniPaciente());
        EmpleadoClientResponse medico = obtenerMedico(rm.getIdMedico());

        return new RecetaMedicaResponse(
                rm.getId(),
                rm.getFechaSolicitud(),
                paciente,
                medico,
                detalles
        );
    }

    private DetalleRecetaResponse toDetalleResponse(DetalleReceta dr) {
        MedicamentosResponse medicamento = obtenerMedicamento(dr.getIdMedicamento());

        return new DetalleRecetaResponse(
                dr.getId(),
                medicamento,
                dr.getDosis(),
                dr.getFrecuencia(),
                dr.getViaAdministracion(),
                dr.getCantidad()
        );
    }

}
