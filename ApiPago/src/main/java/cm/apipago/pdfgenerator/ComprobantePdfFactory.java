package cm.apipago.pdfgenerator;

import cm.apipago.repository.comprobantepago.ComprobantePago;

import java.util.Map;

public class ComprobantePdfFactory {

    private static final Map<ComprobantePago.TipoComprobante, ComprobantePdfGenerator> generators = Map.of(
            ComprobantePago.TipoComprobante.BOLETA, new BoletaPdfGenerator(),
            ComprobantePago.TipoComprobante.FACTURA, new FacturaPdfGenerator()
    );

    public static ComprobantePdfGenerator getGenerator(ComprobantePago.TipoComprobante tipo) {
        return generators.get(tipo);
    }

}
