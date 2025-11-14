package cm.apipago.service;

import cm.apipago.client.citamedica.CitaMedicaFeignClient;
import cm.apipago.client.citamedica.CitaMedicaFeignResponse;
import cm.apipago.dto.pagocita.PagoCitaRequest;
import cm.apipago.dto.pagocita.PagoCitaResponse;
import cm.apipago.repository.pagocita.PagoCita;
import cm.apipago.repository.pagocita.PagoCitaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoCitaService {

    private final PagoCitaRepository repository;
    private final CitaMedicaFeignClient citaMedicaClient;

    /**
     * Registra un Pago de Cita en la BD luego
     * marcarse como COMPLETADA una Cita Médica
     *
     * @param request Objeto {@link PagoCitaRequest} que contiene los datos
     *                requeridos para registrar el Pago de Cita
     */
    @Transactional
    public void registrar(PagoCitaRequest request) {
        log.info("Iniciando registro de pago de cita para cita ID: {}, DNI: {}",
                request.idCitaMedica(), request.dniPaciente());

        PagoCita pagoCita = PagoCita.builder()
                .idCitaMedica(request.idCitaMedica())
                .comprobantePago(null)
                .dniPaciente(request.dniPaciente())
                .montoTotal(request.montoTotal())
                .metodoPago(request.metodoPago())
                .build();

        log.debug("Pago creado - Monto: {}, Método: {}",
                request.montoTotal(), request.metodoPago());

        repository.save(pagoCita);

        log.info("Pago de cita registrado exitosamente. ID Pago: {}, Cita ID: {}",
                pagoCita.getId(), request.idCitaMedica());
    }

    @Transactional(readOnly = true)
    public List<PagoCitaResponse> buscarPorDniPaciente(String dni) {
        log.info("Inicio de búsqueda de todos por DNI: {}", dni);
        List<PagoCita> pagoCitas = repository.findAllByDniPaciente(dni);
        log.info("Pago Citas encontrados: {}", pagoCitas.size());

        return pagoCitas.stream()
                .map(this::toResponse)
                .toList();
    }

    // SERVICIOS PARA OBTENER DATOS DE OTROS MICROSERVICIOS

    /**
     * Obtiene los datos de la/las Citas Médicas asociadas al pago de cita.
     * Si no se encuentra la Cita Médica (404 NotFound) lanzará una excepción
     *
     * @param idCitaMedica Identificador único de la Cita Médica
     * @return Objeto {@link CitaMedicaFeignResponse} que contiene los datos de la Cita Médica
     */
    public CitaMedicaFeignResponse obtenerCitaMedica(Long idCitaMedica) {
        if (idCitaMedica == null || idCitaMedica <= 0) {
            log.warn("Intento de obtener cita médica con ID inválido: {}", idCitaMedica);
            throw new IllegalArgumentException("ID inválido");
        }

        return citaMedicaClient.obtenerCita(idCitaMedica);
    }

    // MAPEADORES A DTO

    private PagoCitaResponse toResponse(PagoCita p) {
        CitaMedicaFeignResponse cita = obtenerCitaMedica(p.getIdCitaMedica());

        return new PagoCitaResponse(
                p.getId(),
                cita,
                p.getDniPaciente(),
                p.getMontoTotal(),
                p.getMetodoPago(),
                p.getEstado()
        );
    }

}
