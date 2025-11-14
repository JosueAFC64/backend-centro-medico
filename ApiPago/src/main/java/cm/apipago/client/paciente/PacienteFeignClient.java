package cm.apipago.client.paciente;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ApiPaciente")
public interface PacienteFeignClient {

    @GetMapping("/pacientes/feign/{dni}")
    PacienteClientResponse obtenerDatosSimples(@PathVariable String dni);

}
