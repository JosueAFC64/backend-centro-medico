package cm.apipago.service;

import cm.apipago.client.citamedica.CitaMedicaFeignClient;
import cm.apipago.client.citamedica.CitaMedicaFeignResponse;
import cm.apipago.client.paciente.PacienteClientResponse;
import cm.apipago.client.paciente.PacienteFeignClient;
import cm.apipago.dto.comprobantepago.ComprobantePagoRequest;
import cm.apipago.dto.comprobantepago.ComprobantePagoResponse;
import cm.apipago.dto.pagocita.PagoCitaResponse;
import cm.apipago.pdfgenerator.ComprobantePdfFactory;
import cm.apipago.pdfgenerator.ComprobantePdfGenerator;
import cm.apipago.repository.comprobantepago.ComprobantePago;
import cm.apipago.repository.comprobantepago.ComprobantePagoRepository;
import cm.apipago.repository.pagocita.PagoCita;
import cm.apipago.repository.pagocita.PagoCitaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComprobantePagoService {

    private final ComprobantePagoRepository repository;
    private final PagoCitaRepository pagoCitaRepository;
    private final PacienteFeignClient pacienteClient;
    private final CitaMedicaFeignClient citaMedicaClient;
    private static final BigDecimal IGV_RATE = new BigDecimal("0.18");

    /**
     * Crea un objeto {@link ComprobantePago} para uno o más objetos {@link PagoCita}
     * y las marca como PAGADA
     *
     * @param request Objeto {@link ComprobantePagoRequest} que contiene los datos requeridos
     *                para generar el comprobante de pago
     * @return Objeto {@link ComprobantePagoResponse} que contiene los datos del
     * comprobante de pago ya generado
     * @throws IllegalStateException Si el ID de {@link PagoCita} ya tiene un {@link ComprobantePago} asociado
     * @throws RuntimeException Si ocurre un error inesperado durante el proceso
     */
    @Transactional
    public byte[] registrar(ComprobantePagoRequest request) {
        log.info("Iniciando registro de comprobante de pago para ID{}", request.idPagoCita());

        PagoCita pagoCita = pagoCitaRepository.findById(request.idPagoCita())
                .orElseThrow(() -> {
                    log.warn("Pago cita con ID: {} no encontrado", request.idPagoCita());
                    return new EntityNotFoundException("Pago cita con ID: " + request.idPagoCita() + " no encontrado");
                });

        log.debug("Pago cita con ID: {} encontrado", pagoCita.getId());

        try {
            log.debug("Obteniendo datos del paciente con DNI: {}", request.dniPaciente());
            PacienteClientResponse paciente = obtenerDatosPaciente(request.dniPaciente());
            log.debug("Datos del paciente obtenidos correctamente: {}", paciente.nombreCompleto());

            BigDecimal total = pagoCita.getMontoTotal().multiply(BigDecimal.ONE.add(IGV_RATE));
            log.debug("Total calculado con IGV: {}", total);

            ComprobantePago comprobantePago = ComprobantePago.builder()
                    .pagoCita(pagoCita)
                    .tipoComprobante(request.tipoComprobante())
                    .dniPaciente(paciente.dni())
                    .nombrePaciente(paciente.nombreCompleto())
                    .direccionPaciente(paciente.direccion())
                    .subtotal(pagoCita.getMontoTotal())
                    .igv(IGV_RATE)
                    .total(total.setScale(2, RoundingMode.HALF_UP))
                    .fechaEmision(LocalDateTime.now())
                    .build();

            repository.save(comprobantePago);
            log.info("Comprobante base creado con ID: {}", comprobantePago.getId());

            pagoCita.setEstado(PagoCita.EstadoPago.PAGADO);

            pagoCitaRepository.save(pagoCita);
            log.debug("Estados de pago actualizados para: {}", pagoCita.getId());

            String serie = comprobantePago.getTipoComprobante() == ComprobantePago.TipoComprobante.FACTURA
                    ? "F001" : "B001";
            String numero = String.format("%08d", comprobantePago.getId());
            comprobantePago.setSerie(serie);
            comprobantePago.setNumeroComprobante(numero);
            comprobantePago.setNumeroCompletoComprobante(serie + "-" + numero);

            repository.save(comprobantePago);
            log.info("Comprobante completado con número: {}", comprobantePago.getNumeroCompletoComprobante());

            ComprobantePagoResponse response = toResponse(comprobantePago);
            log.info("Comprobante registrado exitosamente. ID: {}, Total: {}", comprobantePago.getId(), total);

            ComprobantePdfGenerator generator = ComprobantePdfFactory
                    .getGenerator(comprobantePago.getTipoComprobante());

            if (generator == null) throw new IllegalArgumentException(
                    "Tipo de comprobante no soportado: " + comprobantePago.getTipoComprobante());

            return generator.generarPdf(response);

        } catch (Exception e) {
            log.error("Error al generar el comprobante para pago: {}", request.idPagoCita(), e);

            if (pagoCita.getComprobantePago() == null) {
                pagoCita.setEstado(PagoCita.EstadoPago.FALLIDO);
                pagoCitaRepository.save(pagoCita);
                log.warn(
                        "Pago ID: {} marcado como FALLIDO debido al error en la generación del comprobante",
                        pagoCita.getId()
                );
            }

            throw new RuntimeException("Error al generar el comprobante: " + e.getMessage(), e);
        }
    }

    // SERVICIOS PARA OBTENER DATOS DE OTROS MICROSERVICIOS

    /**
     * Obtiene los datos del Paciente asociado al comprobante de pago
     * a través del cliente {@code pacienteClient}. Si no se encuentra al
     * Paciente (404 NotFound), devolverá solo su DNI (resto de campos = null)
     *
     * @param dni DNI único del Paciente
     * @return Objeto {@link PacienteClientResponse} que contiene los datos del Paciente
     */
    public PacienteClientResponse obtenerDatosPaciente(String dni) {
        if (dni == null || dni.isEmpty()) {
            log.warn("Intento de obtener paciente con DNI inválido: {}", dni);
            throw new IllegalArgumentException("DNI inválido");
        }

        return pacienteClient.obtenerDatosSimples(dni);
    }

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

    private ComprobantePagoResponse toResponse(ComprobantePago comprobante) {

        PagoCitaResponse pagoCita = toPagoCitaResponse(comprobante.getPagoCita());

        return new ComprobantePagoResponse(
                comprobante.getNumeroCompletoComprobante(),
                pagoCita,
                comprobante.getDniPaciente(),
                comprobante.getNombrePaciente(),
                comprobante.getDireccionPaciente(),
                comprobante.getSubtotal(),
                comprobante.getIgv(),
                comprobante.getTotal(),
                comprobante.getFechaEmision()
        );
    }

    private PagoCitaResponse toPagoCitaResponse(PagoCita p) {
        CitaMedicaFeignResponse cita = obtenerCitaMedica(p.getIdCitaMedica());

        return new PagoCitaResponse(
                p.getId(),
                cita,
                p.getMontoTotal(),
                p.getMetodoPago(),
                p.getEstado()
        );
    }

}
