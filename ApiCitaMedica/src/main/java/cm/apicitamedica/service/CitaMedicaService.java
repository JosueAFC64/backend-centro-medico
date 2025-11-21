package cm.apicitamedica.service;

import cm.apicitamedica.client.paciente.PacienteFeignClient;
import cm.apicitamedica.client.paciente.PacienteSimpleResponse;
import cm.apicitamedica.client.pagocita.PagoCitaFeignClient;
import cm.apicitamedica.client.pagocita.PagoCitaRequest;
import cm.apicitamedica.client.slot.DetalleHorarioFeignClient;
import cm.apicitamedica.client.slot.SlotClientResponse;
import cm.apicitamedica.dto.CitaMedicaFeignResponse;
import cm.apicitamedica.dto.CitaMedicaRequest;
import cm.apicitamedica.dto.CitaMedicaResponse;
import cm.apicitamedica.repository.CitaMedica;
import cm.apicitamedica.repository.CitaMedicaRepository;
import com.itextpdf.text.pdf.draw.LineSeparator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CitaMedicaService {

    private final CitaMedicaRepository repository;
    private final PacienteFeignClient pacienteClient;
    private final DetalleHorarioFeignClient detallesClient;
    private final PagoCitaFeignClient pagoCitaClient;

    // SERVICIOS CRUD

    /**
     * Registra una cita médica en la base de datos
     *
     * @param request Objeto {@link CitaMedicaRequest} que contiene los datos requeridos para registrar la cita
     * @return Objeto {@link CitaMedicaResponse} que contiene los datos de la cita ya registrada
     * @throws IllegalStateException Si el Slot del horario donde se registrará la cita
     * no está disponible
     * @throws IllegalArgumentException Si el idHorario o idDetalle usado en {@code obtenerSlot} es inválido
     */
    @Transactional
    public byte[] registrar(CitaMedicaRequest request) {
        log.info("Inicio de proceso de registro para DNI: {}", request.dniPaciente());

        SlotClientResponse slot = obtenerSlot(request.idHorario(), request.idDetalleHorario());

        log.debug("Realizando validaciones");
        validarSlot(request.idHorario(), request.idDetalleHorario(), null, request.dniPaciente(), slot.estado());
        log.debug("Validaciones sin excepciones encontradas");

        BigDecimal costo = slot.especialidad().costo();

        log.debug("Creando entidad CitaMedica para: {}", request);
        CitaMedica citaMedica = CitaMedica.builder()
                .dniPaciente(request.dniPaciente())
                .idHorario(request.idHorario())
                .idDetalleHorario(request.idDetalleHorario())
                .costo(costo)
                .build();

        CitaMedica c = repository.save(citaMedica);
        log.info("Cita con ID: {} registrada correctamente", c.getId());

        log.debug("Ocupando Slot con ID: {} con idCita: {}", request.idDetalleHorario(), c.getId());
        ocuparSlot(request.idHorario(), request.idDetalleHorario(), c.getId());
        log.debug("Slot con ID: {} ocupado correctamente con idCita: {}", request.idDetalleHorario(), c.getId());

        log.debug("Registrando pago de cita para Cita Médica: {}", citaMedica.getId());
        registrarPagoCita(citaMedica.getId(), citaMedica.getDniPaciente(), citaMedica.getCosto(), request.metodoPago());
        log.debug("Pago de cita registrado para Cita Médica: {}", citaMedica.getId());

        return generarPdfCita(toResponse(c));
    }

    /**
     * Busca una cita por idHorario e idDetalle
     *
     * @param idHorario Identificador único del horario
     * @param idDetalle Identificador único del slot del horario
     * @return Objeto {@link CitaMedicaResponse} que contiene los datos de la cita
     * @throws EntityNotFoundException Si no se encuentra la cita con el ID brindado
     */
    @Transactional(readOnly = true)
    public CitaMedicaResponse buscarPorHorarioSlot(Long idHorario, Long idDetalle) {
        log.info("Inicio de proceso de buscar por idHorario: {} e idDetalle: {}", idHorario, idDetalle);

        CitaMedica cita = repository.findByIdHorarioAndIdDetalleHorario(idHorario, idDetalle)
                .orElseThrow(() -> {
                    log.warn("No existe una cita con idHorario: {} o idDetalle: {}", idHorario, idDetalle);
                    return new EntityNotFoundException(
                            "Cita con idHorario: " + idHorario + "o idDetalle: " + idDetalle + " no encontrada"
                    );
                });
        log.info("Cita con ID: {} encontrada", cita.getId());

        return toResponse(cita);
    }

    /**
     * Busca todas las citas pendientes de una Paciente por DNI
     *
     * @param dni DNI del paciente
     * @return Lista de objetos {@link CitaMedicaResponse} que contiene los datos de las citas
     * @throws EntityNotFoundException Si no se encuentra la cita con el DNI brindado
     */
    @Transactional(readOnly = true)
    public List<CitaMedicaResponse> buscarPorDniPaciente(String dni) {
        log.info("Inicio de proceso de buscar por DNI: {}", dni);

        List<CitaMedica> citas = repository.findAllByDniPacienteAndEstado(
                dni,
                CitaMedica.EstadoCitaMedica.PENDIENTE
        );
        log.info("Citas encontradas correctamente: {}", citas.size());

        return citas.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Actualiza los datos de una cita médica
     *
     * @param request Objeto {@link CitaMedicaRequest} que contiene los datos actualizados de la cita
     * @param id Identificador único de la cita
     * @return Objeto {@link CitaMedicaResponse} que contiene los datos ya actualizados de la cita
     * @throws EntityNotFoundException Si no se encuentra una cita con el ID brindado
     * @throws IllegalArgumentException Si idHorario o idDetalle es inválido
     */
    @Transactional(readOnly = true)
    public CitaMedicaResponse actualizar(CitaMedicaRequest request, Long id) {

        log.info("Inicio de proceso de actualizar con ID: {}", id);

        SlotClientResponse slot = obtenerSlot(request.idHorario(), request.idDetalleHorario());
        log.debug("Slot con ID: {} encontrado", request.idDetalleHorario());

        CitaMedica cita = repository.findById(id)
                        .orElseThrow(() -> {
                            log.warn("Cita con ID: {} no encontrada", id);
                            return new EntityNotFoundException("Cita con id: " + id + "no encontrada");
                        });

        log.debug("Realizando validaciones");
        validarSlot(
                request.idHorario(),
                request.idDetalleHorario(),
                cita.getId(),
                request.dniPaciente(),
                slot.estado()
        );
        log.debug("Validaciones sin excepciones encontradas");

        if (!cita.getDniPaciente().equals(request.dniPaciente())) {
            log.debug("Actualizando DNI de Paciente de: {} a: {}", cita.getDniPaciente(), request.dniPaciente());
            cita.setDniPaciente(request.dniPaciente());
        }

        if (!cita.getIdHorario().equals(request.idHorario())) {
            log.debug("Actualizando idHorario de: {} a:{}", cita.getIdHorario(), request.idHorario());
            cita.setIdHorario(request.idHorario());
        }

        if (!cita.getIdDetalleHorario().equals(request.idDetalleHorario())) {
            log.debug("Actualizando idDetalle de: {} a: {}", cita.getIdDetalleHorario(), request.idDetalleHorario());
            cita.setIdDetalleHorario(request.idDetalleHorario());
        }

        if (cambioSlot(cita, request)) {
            liberarSlot(cita.getIdHorario(), cita.getIdDetalleHorario());
        }

        repository.save(cita);

        if (cambioSlot(cita, request)) {
            ocuparSlot(request.idHorario(), request.idDetalleHorario(), cita.getId());
        }

        log.info("Cita con ID: {} actualizada correctamente", id);

        return toResponse(cita);
    }

    /**
     * Elimina una cita y libera el slot de horario asociado a ella
     *
     * @param id Identificador único de la cita
     * @param idHorario Identificador único del horario
     * @param idDetalle Identificador único del slot del horario
     * @throws EntityNotFoundException Si no se encuentra una cita con el ID brindado
     */
    @Transactional
    public void eliminar(Long id, Long idHorario, Long idDetalle) {
        log.info("Inicio de proceso de eliminar con ID: {}", id);
        if (!repository.existsById(id)) {
            log.warn("Cita con ID: {} no encontrada", id);
            throw new EntityNotFoundException("Cita con id: " + id + "no encontrada");
        }

        repository.deleteById(id);
        log.info("Cita con ID: {} eliminada correctamente", id);
        liberarSlot(idHorario, idDetalle);
        log.debug("Slot con ID: {} liberado correctamente", idDetalle);
    }

    /**
     * Cancela una Cita y libera el Slot asociado a ella
     *
     * @param id Identificador único de la cita
     * @return Objeto {@link CitaMedicaResponse} que contiene los datos de la cita
     * @throws EntityNotFoundException Si no se encuentra una cita con el ID brindado
     */
    @Transactional
    public CitaMedicaResponse cancelarCita(Long id) {
        log.info("Inicio de cancelar cita con ID: {}", id);

        CitaMedica cita = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cita con ID: {} no encontrada", id);
                    return new EntityNotFoundException("Cita con id: " + id + "no encontrada");
                });

        if (cita.getEstado() != CitaMedica.EstadoCitaMedica.CANCELADA) {
            cita.cancelarCita();
            liberarSlot(cita.getIdHorario(), cita.getIdDetalleHorario());
        }

        repository.save(cita);
        log.info("Cita con ID: {} cancelada correctamente", cita.getEstado());

        return toResponse(cita);
    }

    /**
     * Completa una Cita y Registra un Pago de la misma en ApiPago
     *
     * @param id Identificador único de la cita
     * @throws EntityNotFoundException Si no se encuentra una cita con el ID brindado
     */
    @Transactional
    public void completarCita(Long id) {
        log.info("Inicio de completar cita con ID: {}", id);

        CitaMedica cita = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cita con ID: {} no encontrada", id);
                    return new EntityNotFoundException("Cita con id: " + id + "no encontrada");
                });

        cita.completarCita();

        repository.save(cita);
        log.info("Cita con ID: {} cancelada correctamente", cita.getEstado());
    }

    /**
     * Genera un PDF con los datos de la Cita Médica
     * para entregar al Paciente
     *
     * @param cita Objeto {@link CitaMedicaResponse} que contiene los datos de la Cita Médica
     * @return PDF con los datos de la Cita Médica
     */
    private byte[] generarPdfCita(CitaMedicaResponse cita) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Definición de fuentes y colores
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, BaseColor.WHITE);
            Font subtituloFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(41, 128, 185));
            Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, new BaseColor(127, 140, 141));
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.DARK_GRAY);
            Font destacadoFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(41, 128, 185));

            // ============ ENCABEZADO CON FONDO AZUL ============
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);
            PdfPCell headerCell = new PdfPCell();
            headerCell.setBackgroundColor(new BaseColor(41, 128, 185));
            headerCell.setBorder(Rectangle.NO_BORDER);
            headerCell.setPadding(15);

            Paragraph headerText = new Paragraph("COMPROBANTE DE CITA MÉDICA", tituloFont);
            headerText.setAlignment(Element.ALIGN_CENTER);
            headerCell.addElement(headerText);

            Paragraph codigoCita = new Paragraph("N° " + (cita.id() != null ? String.format("%06d", cita.id()) : "------"),
                    new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.WHITE));
            codigoCita.setAlignment(Element.ALIGN_CENTER);
            codigoCita.setSpacingBefore(5);
            headerCell.addElement(codigoCita);

            headerTable.addCell(headerCell);
            document.add(headerTable);
            document.add(new Paragraph(" "));

            // ============ INFORMACIÓN DEL PACIENTE ============
            document.add(new Paragraph("DATOS DEL PACIENTE", subtituloFont));
            document.add(new Paragraph(" ", normalFont));

            PdfPTable pacienteTable = new PdfPTable(2);
            pacienteTable.setWidthPercentage(100);
            pacienteTable.setWidths(new int[]{1, 2});

            agregarFilaTabla(pacienteTable, "Paciente:",
                    cita.paciente().nombres() + " " + cita.paciente().apellidos(), labelFont, normalFont);
            agregarFilaTabla(pacienteTable, "DNI:", cita.paciente().dni(), labelFont, normalFont);
            agregarFilaTabla(pacienteTable, "Fecha de Nacimiento:",
                    cita.paciente().fechaNacimiento().toString(), labelFont, normalFont);

            document.add(pacienteTable);
            document.add(new Paragraph(" "));

            // ============ LÍNEA SEPARADORA ============
            LineSeparator line = new LineSeparator();
            line.setLineColor(new BaseColor(189, 195, 199));
            document.add(new Chunk(line));
            document.add(new Paragraph(" "));

            // ============ DETALLES DE LA CITA ============
            document.add(new Paragraph("DETALLES DE LA CITA", subtituloFont));
            document.add(new Paragraph(" ", normalFont));

            PdfPTable citaTable = new PdfPTable(2);
            citaTable.setWidthPercentage(100);
            citaTable.setWidths(new int[]{1, 2});

            agregarFilaTabla(citaTable, "Médico:", cita.detalles().medico().nombreCompleto(), labelFont, destacadoFont);
            agregarFilaTabla(citaTable, "Especialidad:", cita.detalles().especialidad().nombre(), labelFont, normalFont);
            agregarFilaTabla(citaTable, "Fecha:", cita.detalles().fecha().toString(), labelFont, destacadoFont);
            agregarFilaTabla(citaTable, "Hora:", cita.detalles().hora().toString(), labelFont, destacadoFont);
            agregarFilaTabla(citaTable, "Consultorio:", "N° " + cita.detalles().consultorio().nro_consultorio(), labelFont, normalFont);
            agregarFilaTabla(citaTable, "Ubicación:", cita.detalles().consultorio().ubicacion(), labelFont, normalFont);

            document.add(citaTable);
            document.add(new Paragraph(" "));

            // ============ LÍNEA SEPARADORA ============
            document.add(new Chunk(line));
            document.add(new Paragraph(" "));

            // ============ INFORMACIÓN DE PAGO ============
            PdfPTable costoTable = new PdfPTable(2);
            costoTable.setWidthPercentage(100);
            costoTable.setWidths(new int[]{3, 1});

            PdfPCell costoLabelCell = new PdfPCell(new Phrase("COSTO DE LA CONSULTA:", labelFont));
            costoLabelCell.setBorder(Rectangle.NO_BORDER);
            costoLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            costoLabelCell.setPaddingRight(10);
            costoTable.addCell(costoLabelCell);

            PdfPCell costoValueCell = new PdfPCell(new Phrase("S/ " +  cita.costo().toString(),
                    new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, new BaseColor(39, 174, 96))));
            costoValueCell.setBorder(Rectangle.NO_BORDER);
            costoValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            costoTable.addCell(costoValueCell);

            document.add(costoTable);
            document.add(new Paragraph(" "));

            // ============ ESTADO DE LA CITA ============
            PdfPTable estadoTable = new PdfPTable(1);
            estadoTable.setWidthPercentage(100);
            PdfPCell estadoCell = new PdfPCell();
            estadoCell.setBackgroundColor(new BaseColor(236, 240, 241));
            estadoCell.setBorder(Rectangle.BOX);
            estadoCell.setBorderColor(new BaseColor(189, 195, 199));
            estadoCell.setPadding(10);
            estadoCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            Paragraph estadoText = new Paragraph("Estado: " + formatearEstado(cita.estado()),
                    new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, new BaseColor(52, 73, 94)));
            estadoText.setAlignment(Element.ALIGN_CENTER);
            estadoCell.addElement(estadoText);

            estadoTable.addCell(estadoCell);
            document.add(estadoTable);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // ============ PIE DE PÁGINA ============
            PdfPTable footerTable = new PdfPTable(1);
            footerTable.setWidthPercentage(100);
            PdfPCell footerCell = new PdfPCell();
            footerCell.setBorder(Rectangle.TOP);
            footerCell.setBorderColor(new BaseColor(189, 195, 199));
            footerCell.setPaddingTop(15);
            footerCell.setBorderWidth(1);

            Paragraph footerText1 = new Paragraph("Gracias por confiar en nuestra clínica",
                    new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, new BaseColor(52, 73, 94)));
            footerText1.setAlignment(Element.ALIGN_CENTER);
            footerCell.addElement(footerText1);

            Paragraph footerText2 = new Paragraph("Por favor, llegue 15 minutos antes de su cita",
                    new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY));
            footerText2.setAlignment(Element.ALIGN_CENTER);
            footerText2.setSpacingBefore(5);
            footerCell.addElement(footerText2);

            footerTable.addCell(footerCell);
            document.add(footerTable);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    /**
     * Metodo auxiliar para el generarPdfCita
     */
    private void agregarFilaTabla(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPaddingBottom(8);
        labelCell.setPaddingTop(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingBottom(8);
        valueCell.setPaddingTop(5);
        table.addCell(valueCell);
    }

    /**
     * Formatea el estado de la Cita Médica a uno más legible
     */
    public String formatearEstado(CitaMedica.EstadoCitaMedica estado) {
        return switch (estado) {
            case PENDIENTE -> "Pendiente";
            case COMPLETADA -> "Completada";
            case CANCELADA -> "Cancelada";
        };
    }

    // SERVICIOS PARA OBTENER DATOS DE OTROS MICROSERVICIOS

    /**
     * Obtiene los siguientes datos del Paciente a través del cliente {@code pacienteClient}:
     * <ul>
     *     <li>ID</li>
     *     <li>Nombres</li>
     *     <li>Apellidos</li>
     *     <li>DNI</li>
     *     <li>Fecha de Nacimiento</li>
     * </ul>
     *
     * Si el Paciente no existe (404 NotFound) solo devolverá su DNI, el resto de campos serán null
     *
     * @param dni DNI del paciente
     * @return Objeto {@link PacienteSimpleResponse} que contiene los datos del paciente
     */
    private PacienteSimpleResponse obtenerPacienteSimple(String dni) {
        PacienteSimpleResponse paciente = pacienteClient.obtenerPacienteSimple(dni);

        if (paciente.idPaciente() == null) {
            log.info("Paciente con DNI {} no registrado en el sistema - Primera visita", dni);
        }

        return paciente;
    }

    /**
     * Obtiene los siguientes datos del Slot a través del cliente {@code detallesClient}:
     * <ul>
     *     <li>ID y nombre completo del médico</li>
     *     <li>ID y nombre de la especialidad</li>
     *     <li>Hora del slot/cita</li>
     *     <li>Número y ubicación del consultorio</li>
     *     <li>Estado del Slot (DISPONIBLE, OCUPADO, BLOQUEADO)</li>
     * </ul>
     *
     * @param idHorario Identificador único del horario
     * @param idDetalle Identificador único del slot del horario
     * @return Objeto {@link SlotClientResponse} que contiene los datos obtenidos a través
     * del cliente {@code detallesClient}
     * @throws IllegalArgumentException Si:
     * <ul>
     *     <li>idHorario es inválido</li>
     *     <li>idDetalle es inválido</li>
     * </ul>
     */
    private SlotClientResponse obtenerSlot(Long idHorario, Long idDetalle) {
        if (idHorario == null || idHorario <= 0) {
            log.error("Intento de obtener nombre con idHorario inválido: {}", idHorario);
            throw new IllegalArgumentException("idHorario inválido");
        }

        if (idDetalle == null || idDetalle <= 0) {
            log.error("Intento de obtener nombre con idDetalle inválido: {}", idDetalle);
            throw new IllegalArgumentException("idDetalle inválido");
        }

        return detallesClient.obtenerSlot(idHorario, idDetalle);
    }

    // SERVICIO PARA BRINDAR DATOS A OTROS MICROSERVICIOS

    /**
     * Ocupa un Slot con el ID de una cita a través del cliente {@code detallesClient},
     * Si ocurre un error durante este proceso, el Slot no se ocupará y se eliminará
     * la cita
     *
     * @param idHorario Identificador único del horario
     * @param idDetalle Identificador único del slot del horario
     * @param idCita Identificador único de la cita
     */
    private void ocuparSlot(Long idHorario, Long idDetalle, Long idCita) {
        if (idHorario == null || idHorario <= 0) {
            log.error("Intento de obtener nombre con idHorario inválido: {}", idHorario);
            throw new IllegalArgumentException("idHorario inválido");
        }

        if (idDetalle == null || idDetalle <= 0) {
            log.error("Intento de obtener nombre con idDetalle inválido: {}", idDetalle);
            throw new IllegalArgumentException("idDetalle inválido");
        }

        if (idCita == null || idCita <= 0) {
            log.error("Intento de obtener nombre con idCita inválido: {}", idCita);
            throw new IllegalArgumentException("idCita inválido");
        }

        try {
            detallesClient.ocuparSlot(idHorario, idDetalle, idCita);
        } catch (Exception e) {
            repository.deleteById(idCita);
            throw new RuntimeException("Error inesperado al ocupar slot del horario", e);
        }
    }

    /**
     * Libera un Slot de un Horario a través del cliente {@code detallesClient}
     * Si ocurre un error durante este proceso el Slot no se liberará y la {@link CitaMedica}
     * permanecerá asociada a este
     *
     * @param idHorario Identificador único del Horario
     * @param idDetalle Identificador único del Slot del Horario
     * @throws IllegalArgumentException Si idHorario o idDetalle es inválido
     */
    private void liberarSlot(Long idHorario, Long idDetalle) {
        if (idHorario == null || idHorario <= 0) {
            log.error("Intento de obtener nombre con idHorario inválido: {}", idHorario);
            throw new IllegalArgumentException("idHorario inválido");
        }

        if (idDetalle == null || idDetalle <= 0) {
            log.error("Intento de obtener nombre con idDetalle inválido: {}", idDetalle);
            throw new IllegalArgumentException("idDetalle inválido");
        }

        try {
            detallesClient.liberarSlot(idHorario, idDetalle);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al liberar slot del horario", e);
        }
    }

    /**
     * Brinda datos de una Cita Médica específica por su ID
     *
     * @param id Identificador único de la Cita Médica
     * @return Objeto {@link CitaMedicaFeignResponse} que contiene los datos de la Cita Médica
     */
    public CitaMedicaFeignResponse brindarCita(Long id) {
        CitaMedica cita = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cita con ID: {} no encontrada", id);
                    return new EntityNotFoundException("Cita Médica con ID: " + id + "no encontrada");
                });

        PacienteSimpleResponse paciente = obtenerPacienteSimple(cita.getDniPaciente());
        CitaMedicaFeignResponse.DatosPaciente datosPaciente = new CitaMedicaFeignResponse.DatosPaciente(
                (paciente.idPaciente() != null) ? paciente.nombres() + " " + paciente.apellidos() : null,
                paciente.dni()
        );


        SlotClientResponse slot = obtenerSlot(cita.getIdHorario(), cita.getIdDetalleHorario());

        return toFeignResponse(cita.getId(),cita.getEstado(), slot, datosPaciente);
    }

    /**
     * Registra un Pago de Cita una vez la Cita Médica
     * haya sido marcada como COMPLETADA
     *
     * @param idCitaMedica Identificador único de la Cita Médica
     * @param dniPaciente DNI único del Paciente
     * @param montoTotal Monto Total de la Cita Médica
     * @param metodoPago Metodo de Pago para la Cita Médica
     * @throws RuntimeException Si ocurre un error durante el proceso
     */
    public void registrarPagoCita(
            Long idCitaMedica,
            String dniPaciente,
            BigDecimal montoTotal,
            String metodoPago
    ) {
        try {
            PagoCitaRequest request = new PagoCitaRequest(
                    idCitaMedica,
                    dniPaciente,
                    montoTotal,
                    metodoPago
            );

            pagoCitaClient.registrarPagoCita(request);
        } catch (Exception e) {
            log.error("Error al registrar el pago Cita: {}", e.getMessage());
            throw new RuntimeException("Error inesperado al registrar pago cita", e);
        }
    }

    // VALIDACIONES

    /**
     * Realiza las siguientes validaciones:
     * <ul>
     *     <li>Que el Slot al que se quiera asociar una cita tenga un estado de DISPONIBLE</li>
     *     <li>Que el Slot no esté asociado a otra cita</li>
     * </ul>
     *
     * @param idHorario Identificador único del horario
     * @param idDetalle Identificador único del slot del horario
     * @param idCita Identificador único de la cita
     * @param dniPaciente DNI del paciente
     * @param estado Estado actual del slot
     * @throws IllegalStateException Si la primera validación no se cumple
     * @throws IllegalArgumentException Si la segunda validación no se cumple
     */
    public void validarSlot(
            Long idHorario,
            Long idDetalle,
            Long idCita,
            String dniPaciente,
            String estado
    ) {
        log.debug("Verificando disponibilidad de Slot con ID: {}", idDetalle);
        if (!"DISPONIBLE".equals(estado)) {
            log.warn("Slot con ID: {} ocupado", idDetalle);
            throw new IllegalStateException("Slot con ID: " + idDetalle + " no está disponible");
        }
        log.debug("Slot con ID: {} disponible", idDetalle);

        boolean existeSolapamiento = (idCita == null)
                ? repository.existeSolapamiento(idHorario, idDetalle, dniPaciente)
                : repository.existeSolapamientoExcluyendoId(idHorario, idDetalle, dniPaciente, idCita);

        if (existeSolapamiento) {
            log.warn("Slot con ID: {} ya está asociado a una Cita", idDetalle);
            throw new IllegalArgumentException(
                    "Ya existe una Cita asociada al Slot con ID: " + idDetalle
            );
        }
    }

    /**
     * Verifica si una Cita ha cambiado o no de Horario y/o Slot
     *
     * @param cita Objeto {@link CitaMedica}
     * @param request Objeto {@link CitaMedicaRequest}
     * @return true o false
     */
    private boolean cambioSlot(CitaMedica cita, CitaMedicaRequest request) {
        return !cita.getIdHorario().equals(request.idHorario()) ||
                !cita.getIdDetalleHorario().equals(request.idDetalleHorario());
    }

    // MAPEADORES A DTO

    private CitaMedicaResponse toResponse(CitaMedica citaMedica) {

        PacienteSimpleResponse paciente = obtenerPacienteSimple(citaMedica.getDniPaciente());
        SlotClientResponse slot = obtenerSlot(citaMedica.getIdHorario(), citaMedica.getIdDetalleHorario());
        CitaMedicaResponse.DetallesCita detallesCita = toDetallesCita(slot);

        return new CitaMedicaResponse(
                citaMedica.getId(),
                paciente,
                citaMedica.getCosto(),
                citaMedica.getEstado(),
                detallesCita
        );
    }

    private CitaMedicaResponse.DetallesCita toDetallesCita(SlotClientResponse slot) {

        CitaMedicaResponse.DatosEspecialidad especialidad = new CitaMedicaResponse.DatosEspecialidad(
                slot.especialidad().id(),
                slot.especialidad().nombre()
        );

        return new CitaMedicaResponse.DetallesCita(
                slot.medico(),
                especialidad,
                slot.fecha(),
                slot.hora(),
                slot.consultorio()
        );
    }

    private CitaMedicaFeignResponse toFeignResponse(Long id,
                                                    CitaMedica.EstadoCitaMedica estado,
                                                    SlotClientResponse slot,
                                                    CitaMedicaFeignResponse.DatosPaciente paciente) {
        return new CitaMedicaFeignResponse(
                id,
                slot.fecha(),
                slot.hora(),
                paciente,
                slot.especialidad().costo(),
                slot.medico().nombreCompleto(),
                slot.especialidad().nombre(),
                estado
        );
    }

}
