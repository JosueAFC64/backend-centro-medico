package cm.apipago.pdfgenerator;

import cm.apipago.dto.comprobantepago.ComprobantePagoResponse;

public interface ComprobantePdfGenerator {

    byte[] generarPdf(ComprobantePagoResponse response);

}
