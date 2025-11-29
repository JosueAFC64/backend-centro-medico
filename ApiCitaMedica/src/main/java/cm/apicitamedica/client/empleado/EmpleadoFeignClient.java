package cm.apicitamedica.client.empleado;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ApiEmpleado", fallbackFactory = EmpleadoFallBackFactory.class)
public interface EmpleadoFeignClient {

    @GetMapping("/empleados/client/{id}")
    EmpleadoClientResponse obtenerNombre(@PathVariable Long id);

}


