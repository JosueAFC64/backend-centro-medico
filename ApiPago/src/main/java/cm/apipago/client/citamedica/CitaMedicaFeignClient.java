package cm.apipago.client.citamedica;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ApiCitaMedica", fallbackFactory = CitaMedicaFallBackFactory.class)
public interface CitaMedicaFeignClient {

    @GetMapping("/citas-medicas/feign/{id}")
    CitaMedicaFeignResponse obtenerCita(@PathVariable Long id);

}
