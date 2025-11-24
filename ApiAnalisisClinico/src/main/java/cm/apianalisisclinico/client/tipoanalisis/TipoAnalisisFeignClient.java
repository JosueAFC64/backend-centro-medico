package cm.apianalisisclinico.client.tipoanalisis;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ApiTipoAnalisis", fallbackFactory = TipoAnalisisFallBackFactory.class)
public interface TipoAnalisisFeignClient {

    @GetMapping("/tipo-analisis/{id}")
    TipoAnalisisResponse buscar(@PathVariable Long id);

}
