package cm.apiatencionmedica.client.recetamedica;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ApiRecetaMedica", fallbackFactory = RecetaMedicaFallBackFactory.class)
public interface RecetaMedicaFeignClient {

    @GetMapping("/recetas/feign/{idAtencion}")
    RecetaMedicaResponse obtenerRecetaMedica(@PathVariable Long idAtencion);

}
