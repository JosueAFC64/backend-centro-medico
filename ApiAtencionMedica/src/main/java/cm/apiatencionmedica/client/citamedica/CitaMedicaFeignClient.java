package cm.apiatencionmedica.client.citamedica;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "ApiCitaMedica", fallbackFactory = CitaMedicaFallBackFactory.class)
public interface CitaMedicaFeignClient {

    @GetMapping("/citas-medicas/feign/{id}")
    CitaMedicaFeignResponse obtenerCita(@PathVariable Long id);

    @PutMapping("/citas-medicas/completar/{id}")
    void completarCita(@PathVariable Long id);

}
