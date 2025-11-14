package cm.apicitamedica.client.paciente;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ApiPaciente", fallbackFactory = PacienteFallBackFactory.class)
public interface PacienteFeignClient {

    @GetMapping("/pacientes/simple/dni/{dni}")
    PacienteSimpleResponse obtenerPacienteSimple(@PathVariable String dni);

}
