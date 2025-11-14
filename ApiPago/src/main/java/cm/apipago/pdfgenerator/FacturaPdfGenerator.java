package cm.apipago.pdfgenerator;

import cm.apipago.client.citamedica.CitaMedicaFeignResponse;
import cm.apipago.dto.comprobantepago.ComprobantePagoResponse;
import cm.apipago.dto.pagocita.PagoCitaResponse;
import cm.apipago.repository.pagocita.PagoCita;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FacturaPdfGenerator implements ComprobantePdfGenerator {

    @Override
    public byte[] generarPdf(ComprobantePagoResponse response) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, out);
            document.open();

            // Encabezado principal
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

            // Título
            Paragraph title = new Paragraph("FACTURA ELECTRÓNICA", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));

            // Información del comprobante y cliente en tabla
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new int[]{1, 1});

            // Columna izquierda - Datos del comprobante
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(Rectangle.BOX);
            leftCell.setPadding(10);
            leftCell.addElement(new Phrase("COMPROBANTE", headerFont));
            leftCell.addElement(new Phrase("N°: " + response.numeroComprobante(), normalFont));
            leftCell.addElement(new Phrase("Fecha: " + formatFecha(response.fechaEmision()), normalFont));

            // Columna derecha - Datos del cliente
            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(Rectangle.BOX);
            rightCell.setPadding(10);
            rightCell.addElement(new Phrase("DATOS DEL CLIENTE", headerFont));
            rightCell.addElement(new Phrase("Nombre: " + response.nombrePaciente(), normalFont));
            rightCell.addElement(new Phrase("DNI/RUC: " + response.dniPaciente(), normalFont));
            if (response.direccionPaciente() != null && !response.direccionPaciente().isEmpty()) {
                rightCell.addElement(new Phrase("Dirección: " + response.direccionPaciente(), normalFont));
            }

            infoTable.addCell(leftCell);
            infoTable.addCell(rightCell);
            document.add(infoTable);

            document.add(new Paragraph(" "));

            // Título de detalle
            Paragraph detalleTitle = new Paragraph("DETALLE DEL SERVICIO", headerFont);
            document.add(detalleTitle);
            document.add(new Paragraph(" "));

            // Tabla de servicios
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.5f, 2f, 1.5f, 1.5f, 1.5f});

            // Encabezados de la tabla
            String[] headers = {"Cita Médica", "Especialidad", "Fecha", "Método Pago", "Monto"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, boldFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Contenido de la tabla
            CitaMedicaFeignResponse cita = response.pagoCita().citaMedica();
            PagoCitaResponse pago = response.pagoCita();

            // Columna 1: Médico y hora
            String citaInfo = cita.medico() + "\n" + cita.hora().toString();
            PdfPCell cell1 = new PdfPCell(new Phrase(citaInfo, normalFont));
            cell1.setPadding(5);
            table.addCell(cell1);

            // Columna 2: Especialidad
            PdfPCell cell2 = new PdfPCell(new Phrase(cita.especialidad(), normalFont));
            cell2.setPadding(5);
            table.addCell(cell2);

            // Columna 3: Fecha
            PdfPCell cell3 = new PdfPCell(new Phrase(cita.fecha().toString(), normalFont));
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell3.setPadding(5);
            table.addCell(cell3);

            // Columna 4: Metodo de pago
            PdfPCell cell4 = new PdfPCell(new Phrase(formatMetodoPago(pago.metodoPago()), normalFont));
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell4.setPadding(5);
            table.addCell(cell4);

            // Columna 5: Monto
            PdfPCell cell5 = new PdfPCell(new Phrase("S/ " + pago.montoTotal().toString(), normalFont));
            cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell5.setPadding(5);
            table.addCell(cell5);


            document.add(table);
            document.add(new Paragraph(" "));

            // Totales alineados a la derecha
            PdfPTable totalsTable = new PdfPTable(2);
            totalsTable.setWidthPercentage(40);
            totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalsTable.setWidths(new float[]{2f, 1.5f});

            addTotalRow(totalsTable, "Subtotal:", "S/ " + response.subtotal().toString(), normalFont);
            addTotalRow(totalsTable, "IGV (18%):", "S/ " +
                            response.subtotal().multiply(response.igv()).setScale(2, RoundingMode.HALF_UP),
                            normalFont);
            addTotalRow(totalsTable, "TOTAL:", "S/ " + response.total().toString(), boldFont);

            document.add(totalsTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de factura", e);
        }
    }

    private void addTotalRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(3);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(3);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private String formatFecha(LocalDateTime fechaEmision) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaEmision.format(formatter);
    }

    private String formatMetodoPago(PagoCita.MetodoPago metodoPago) {
        return switch (metodoPago) {
            case EFECTIVO -> "Efectivo";
            case TARJETA_CREDITO -> "T. Crédito";
            case TARJETA_DEBITO -> "T. Débito";
            case TRANSFERENCIA -> "Transferencia";
        };
    }

}
