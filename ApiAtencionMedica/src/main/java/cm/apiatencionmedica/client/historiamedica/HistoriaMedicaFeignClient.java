package cm.apiatencionmedica.client.historiamedica;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "ApiHistoriaMedica")
public interface HistoriaMedicaFeignClient {

    @PutMapping("/historias/feign/{dniPaciente}/{idAtencion}")
    void registrarAtencionMedica(@PathVariable String dniPaciente, @PathVariable Long idAtencion);

}
