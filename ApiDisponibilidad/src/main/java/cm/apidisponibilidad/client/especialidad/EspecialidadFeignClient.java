package cm.apidisponibilidad.client.especialidad;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ApiEspecialidad", fallbackFactory = EspecialidadFallBackFactory.class)
public interface EspecialidadFeignClient {

    // Obtiene el ID y nombre de la especialidad
    @GetMapping("/especialidades/{id}")
    EspecialidadResponse obtenerEspecialidad(@PathVariable("id") Long id);

}
