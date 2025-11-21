package com.CentroMedico.ApiHistoriaMedica.client.atencionmedica;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ApiAtencionMedica", fallbackFactory = AtencionMedicaFallBackFactory.class)
public interface AtencionMedicaFeignClient {

    @GetMapping("/atenciones-medicas/feign/{id}")
    AtencionMedicaFeignResponse obtenerAtencionMedica(@PathVariable Long id);

}
