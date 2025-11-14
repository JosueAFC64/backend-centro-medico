package cm.apidisponibilidad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApiDisponibilidadApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiDisponibilidadApplication.class, args);
    }

}
