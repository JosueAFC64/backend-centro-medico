package cm.apihorario.client.consultorio;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ApiConsultorio", fallbackFactory = ConsultorioFallBackFactory.class)
public interface ConsultorioFeignClient {

    // Obtiene el nro_consultorio y ubicacion del consultorio
    @GetMapping("/consultorios/client/{nro_consultorio}")
    ConsultorioResponse obtenerConsultorio(@PathVariable("nro_consultorio") String nro_consultorio);

}
