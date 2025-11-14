package cm.apiempleado.client.usuario;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ApiUsuarios")
public interface UserFeignClient {

    @PostMapping("/usuarios")
    void registrar (@RequestBody UserRequest request);

}
