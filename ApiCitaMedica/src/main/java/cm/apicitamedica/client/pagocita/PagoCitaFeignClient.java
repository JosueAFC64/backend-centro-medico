package cm.apicitamedica.client.pagocita;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ApiPago")
public interface PagoCitaFeignClient {

    @PostMapping("/pago-cita")
    void registrarPagoCita(@RequestBody PagoCitaRequest request);

}
