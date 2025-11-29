package cm.apiatencionmedica.client.analisisclinico;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ApiAnalisisClinico", fallbackFactory = AnalisisClinicoFallBackFactory.class)
public interface AnalisisClinicoFeignClient {

    @GetMapping("/analisis/feign/{idAtencion}")
    AnalisisClinicoResponse obtenerAnalisisClinico(@PathVariable Long idAtencion);

}
