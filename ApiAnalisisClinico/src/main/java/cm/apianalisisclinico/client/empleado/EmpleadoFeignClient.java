package cm.apianalisisclinico.client.empleado;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ApiEmpleado", fallbackFactory = EmpleadoFallBackFactory.class)
public interface EmpleadoFeignClient {

    // Obtiene el ID y nombre completo del empleado
    @GetMapping("/empleados/client/{id}")
    EmpleadoClientResponse obtenerNombre(@PathVariable Long id);

}
