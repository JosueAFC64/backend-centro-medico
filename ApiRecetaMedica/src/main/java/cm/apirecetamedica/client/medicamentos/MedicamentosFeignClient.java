package cm.apirecetamedica.client.medicamentos;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ApiMedicamentos")
public interface MedicamentosFeignClient {

    @GetMapping("/medicamentos/{id}")
    MedicamentosResponse obtenerMedicamento(@PathVariable Long id);

}
