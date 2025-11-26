package cm.apianalisisclinico.service;

import cm.apianalisisclinico.client.paciente.PacienteSimpleResponse;
import cm.apianalisisclinico.dto.analisisclinico.AnalisisClinicoResponse;
import cm.apianalisisclinico.dto.detalleanalisis.DetalleAnalisisResponse;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.borders.SolidBorder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class AnalisisClinicoPdfService {

    private static final DeviceRgb COLOR_PRIMARIO = new DeviceRgb(41, 128, 185);
    private static final DeviceRgb COLOR_SECUNDARIO = new DeviceRgb(52, 73, 94);
    private static final DeviceRgb COLOR_FONDO_CLARO = new DeviceRgb(236, 240, 241);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generarOrdenAnalisis(AnalisisClinicoResponse analisis) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.setMargins(40, 40, 40, 40);

            // Fuentes
            PdfFont fontBold = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont fontRegular = PdfFontFactory.createFont("Helvetica");

            // Encabezado
            agregarEncabezado(document, fontBold, analisis);

            // Información del paciente
            agregarInformacionPaciente(document, fontBold, fontRegular, analisis);

            // Información del médico
            agregarInformacionMedico(document, fontBold, fontRegular, analisis);

            // Tabla de análisis solicitados
            agregarTablaAnalisis(document, fontBold, fontRegular, analisis);

            // Pie de página
            agregarPiePagina(document, fontRegular);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de orden de análisis", e);
        }
    }

    private void agregarEncabezado(Document document, PdfFont fontBold, AnalisisClinicoResponse analisis) {
        Paragraph titulo = new Paragraph("ORDEN DE ANÁLISIS CLÍNICO")
                .setFont(fontBold)
                .setFontSize(20)
                .setFontColor(COLOR_PRIMARIO)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(titulo);

        Paragraph numeroOrden = new Paragraph("N° " + String.format("%06d", analisis.id()))
                .setFont(fontBold)
                .setFontSize(12)
                .setFontColor(COLOR_SECUNDARIO)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(numeroOrden);
    }

    private void agregarInformacionPaciente(Document document, PdfFont fontBold,
                                            PdfFont fontRegular, AnalisisClinicoResponse analisis) {
        // Título de sección
        Paragraph tituloPaciente = new Paragraph("DATOS DEL PACIENTE")
                .setFont(fontBold)
                .setFontSize(12)
                .setFontColor(ColorConstants.WHITE)
                .setBackgroundColor(COLOR_PRIMARIO)
                .setPadding(8)
                .setMarginBottom(10);
        document.add(tituloPaciente);

        // Tabla de datos del paciente
        Table tablaPaciente = new Table(UnitValue.createPercentArray(new float[]{1, 3}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        PacienteSimpleResponse paciente = analisis.paciente();
        String nombreCompleto = paciente.nombres() + " " + paciente.apellidos();

        agregarFilaDatos(tablaPaciente, "Paciente:", nombreCompleto, fontBold, fontRegular);
        agregarFilaDatos(tablaPaciente, "DNI:", paciente.dni(), fontBold, fontRegular);
        agregarFilaDatos(tablaPaciente, "Fecha de Nacimiento:",
                paciente.fechaNacimiento().format(DATE_FORMATTER), fontBold, fontRegular);

        document.add(tablaPaciente);
    }

    private void agregarInformacionMedico(Document document, PdfFont fontBold,
                                          PdfFont fontRegular, AnalisisClinicoResponse analisis) {
        // Título de sección
        Paragraph tituloMedico = new Paragraph("MÉDICO SOLICITANTE")
                .setFont(fontBold)
                .setFontSize(12)
                .setFontColor(ColorConstants.WHITE)
                .setBackgroundColor(COLOR_PRIMARIO)
                .setPadding(8)
                .setMarginBottom(10);
        document.add(tituloMedico);

        // Tabla de datos del médico
        Table tablaMedico = new Table(UnitValue.createPercentArray(new float[]{1, 3}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        agregarFilaDatos(tablaMedico, "Médico:",
                analisis.medico().nombreCompleto(), fontBold, fontRegular);
        agregarFilaDatos(tablaMedico, "Fecha de Solicitud:",
                analisis.fechaSolicitud().format(DATE_FORMATTER), fontBold, fontRegular);

        document.add(tablaMedico);
    }

    private void agregarTablaAnalisis(Document document, PdfFont fontBold,
                                      PdfFont fontRegular, AnalisisClinicoResponse analisis) {
        // Título de sección
        Paragraph tituloAnalisis = new Paragraph("ANÁLISIS SOLICITADOS")
                .setFont(fontBold)
                .setFontSize(12)
                .setFontColor(ColorConstants.WHITE)
                .setBackgroundColor(COLOR_PRIMARIO)
                .setPadding(8)
                .setMarginBottom(10);
        document.add(tituloAnalisis);

        // Tabla de análisis
        Table tablaAnalisis = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(30);

        // Encabezados
        tablaAnalisis.addHeaderCell(crearCeldaEncabezado("N°", fontBold));
        tablaAnalisis.addHeaderCell(crearCeldaEncabezado("Tipo de Análisis", fontBold));
        tablaAnalisis.addHeaderCell(crearCeldaEncabezado("Muestra Requerida", fontBold));

        // Filas de datos
        int numero = 1;
        for (DetalleAnalisisResponse detalle : analisis.detalles()) {
            tablaAnalisis.addCell(crearCeldaDato(String.valueOf(numero++), fontRegular, TextAlignment.CENTER));
            tablaAnalisis.addCell(crearCeldaDato(detalle.tipoAnalisis().nombre(), fontRegular, TextAlignment.LEFT));
            tablaAnalisis.addCell(crearCeldaDato(detalle.tipoAnalisis().muestraRequerida(), fontRegular, TextAlignment.CENTER));
        }

        document.add(tablaAnalisis);
    }

    private void agregarPiePagina(Document document, PdfFont fontRegular) {
        Paragraph notaPie = new Paragraph(
                "Este documento constituye una orden médica oficial. " +
                        "Presente este documento en el laboratorio para realizar los análisis solicitados.")
                .setFont(fontRegular)
                .setFontSize(9)
                .setFontColor(COLOR_SECUNDARIO)
                .setTextAlignment(TextAlignment.CENTER)
                .setItalic()
                .setMarginTop(20);
        document.add(notaPie);
    }

    private void agregarFilaDatos(Table tabla, String etiqueta, String valor,
                                  PdfFont fontBold, PdfFont fontRegular) {
        Cell celdaEtiqueta = new Cell()
                .add(new Paragraph(etiqueta).setFont(fontBold))
                .setBackgroundColor(COLOR_FONDO_CLARO)
                .setPadding(8)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));

        Cell celdaValor = new Cell()
                .add(new Paragraph(valor).setFont(fontRegular))
                .setPadding(8)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));

        tabla.addCell(celdaEtiqueta);
        tabla.addCell(celdaValor);
    }

    private Cell crearCeldaEncabezado(String texto, PdfFont fontBold) {
        return new Cell()
                .add(new Paragraph(texto).setFont(fontBold).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(COLOR_SECUNDARIO)
                .setPadding(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(new SolidBorder(ColorConstants.WHITE, 1));
    }

    private Cell crearCeldaDato(String texto, PdfFont fontRegular, TextAlignment alignment) {
        return new Cell()
                .add(new Paragraph(texto).setFont(fontRegular))
                .setPadding(8)
                .setTextAlignment(alignment)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
    }
}