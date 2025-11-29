package cm.apirecetamedica.service;

import cm.apirecetamedica.client.empleado.EmpleadoClientResponse;
import cm.apirecetamedica.client.paciente.PacienteSimpleResponse;
import cm.apirecetamedica.dto.detallereceta.DetalleRecetaResponse;
import cm.apirecetamedica.dto.recetamedica.RecetaMedicaResponse;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Period;

@Service
public class RecetaMedicaPdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(41, 128, 185);
    private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(245, 245, 245);

    public byte[] generarPdfRecetaMedica(RecetaMedicaResponse receta) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.setMargins(40, 40, 40, 40);

            // Encabezado
            agregarEncabezado(document, receta);

            // Información del paciente
            agregarInformacionPaciente(document, receta.paciente());

            document.add(new Paragraph("\n"));

            // Título Rx
            Paragraph rx = new Paragraph("Rx")
                    .setFontSize(24)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10)
                    .setMarginBottom(10);
            document.add(rx);

            // Detalles de medicamentos
            agregarDetallesMedicamentos(document, receta.detalles());

            document.add(new Paragraph("\n"));

            // Firma del médico
            agregarFirmaMedico(document, receta.medico(), receta.fechaSolicitud());

            // Pie de página
            agregarPiePagina(document);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de receta médica", e);
        }
    }

    private void agregarEncabezado(Document document, RecetaMedicaResponse receta) {
        Paragraph titulo = new Paragraph("RECETA MÉDICA")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(HEADER_COLOR);
        document.add(titulo);

        Paragraph numeroReceta = new Paragraph("N° " + String.format("%06d", receta.id()))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(numeroReceta);

        Paragraph fecha = new Paragraph("Fecha: " + receta.fechaSolicitud().format(DATE_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(15);
        document.add(fecha);
    }

    private void agregarInformacionPaciente(Document document, PacienteSimpleResponse paciente) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{25, 75}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(10);

        table.addCell(crearCeldaEtiqueta("Paciente:"));
        table.addCell(crearCeldaValor(paciente.nombres() + " " + paciente.apellidos()));

        table.addCell(crearCeldaEtiqueta("DNI:"));
        table.addCell(crearCeldaValor(paciente.dni()));

        int edad = calcularEdad(paciente.fechaNacimiento());
        table.addCell(crearCeldaEtiqueta("Edad:"));
        table.addCell(crearCeldaValor(edad + " años"));

        table.addCell(crearCeldaEtiqueta("Fecha Nacimiento:"));
        table.addCell(crearCeldaValor(paciente.fechaNacimiento().format(DATE_FORMATTER)));

        document.add(table);
    }

    private void agregarDetallesMedicamentos(Document document, java.util.List<DetalleRecetaResponse> detalles) {
        for (int i = 0; i < detalles.size(); i++) {
            DetalleRecetaResponse detalle = detalles.get(i);

            // Contenedor del medicamento con fondo gris claro
            Table medicamentoTable = new Table(1)
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(15);

            // Nombre del medicamento (destacado)
            Paragraph nombreMed = new Paragraph()
                    .add(i + 1 + ". ")
                    .add(detalle.medicamento().nombre())
                    .setBold()
                    .setFontSize(13)
                    .setFontColor(HEADER_COLOR);

            Cell headerCell = new Cell()
                    .add(nombreMed)
                    .setBackgroundColor(LIGHT_GRAY)
                    .setBorder(Border.NO_BORDER)
                    .setPadding(8);
            medicamentoTable.addCell(headerCell);

            // Detalles del medicamento
            Table detalleTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                    .setWidth(UnitValue.createPercentValue(100));

            detalleTable.addCell(crearCeldaDetalleEtiqueta("Presentación:"));
            detalleTable.addCell(crearCeldaDetalleValor(detalle.medicamento().presentacion()));

            detalleTable.addCell(crearCeldaDetalleEtiqueta("Dosis:"));
            detalleTable.addCell(crearCeldaDetalleValor(detalle.dosis()));

            detalleTable.addCell(crearCeldaDetalleEtiqueta("Frecuencia:"));
            detalleTable.addCell(crearCeldaDetalleValor(detalle.frecuencia()));

            detalleTable.addCell(crearCeldaDetalleEtiqueta("Vía:"));
            detalleTable.addCell(crearCeldaDetalleValor(detalle.viaAdministracion()));

            detalleTable.addCell(crearCeldaDetalleEtiqueta("Cantidad:"));
            detalleTable.addCell(crearCeldaDetalleValor(String.valueOf(detalle.cantidad())));

            Cell detalleCell = new Cell()
                    .add(detalleTable)
                    .setBorder(new SolidBorder(LIGHT_GRAY, 1))
                    .setPadding(10);
            medicamentoTable.addCell(detalleCell);

            document.add(medicamentoTable);
        }
    }

    private void agregarFirmaMedico(Document document, EmpleadoClientResponse medico, LocalDate fecha) {
        document.add(new Paragraph("\n\n"));

        // Línea para la firma
        Table firmaTable = new Table(1)
                .setWidth(UnitValue.createPercentValue(50))
                .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT);

        Cell lineaFirma = new Cell()
                .add(new Paragraph(""))
                .setBorderTop(new SolidBorder(ColorConstants.BLACK, 1))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setPaddingTop(30);
        firmaTable.addCell(lineaFirma);

        document.add(firmaTable);

        // Información del médico
        Paragraph nombreMedico = new Paragraph(medico.nombreCompleto())
                .setFontSize(11)
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(nombreMedico);

        Paragraph codigoMedico = new Paragraph("CMP: " + medico.id())
                .setFontSize(9)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(codigoMedico);

        // Agregar fecha de emisión
        Paragraph fechaEmision = new Paragraph("Fecha: " + fecha.format(DATE_FORMATTER))
                .setFontSize(9)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(fechaEmision);
    }

    private void agregarPiePagina(Document document) {
        document.add(new Paragraph("\n"));

        Paragraph nota = new Paragraph("Esta receta médica tiene validez de 30 días desde su emisión.")
                .setFontSize(8)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY);
        document.add(nota);
    }

    private Cell crearCeldaEtiqueta(String texto) {
        return new Cell()
                .add(new Paragraph(texto).setBold().setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setBackgroundColor(LIGHT_GRAY)
                .setPadding(5);
    }

    private Cell crearCeldaValor(String texto) {
        return new Cell()
                .add(new Paragraph(texto).setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
    }

    private Cell crearCeldaDetalleEtiqueta(String texto) {
        return new Cell()
                .add(new Paragraph(texto).setBold().setFontSize(9))
                .setBorder(Border.NO_BORDER)
                .setPadding(3);
    }

    private Cell crearCeldaDetalleValor(String texto) {
        return new Cell()
                .add(new Paragraph(texto).setFontSize(9))
                .setBorder(Border.NO_BORDER)
                .setPadding(3);
    }

    private int calcularEdad(LocalDate fechaNacimiento) {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}