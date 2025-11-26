package cm.apirecetamedica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApiRecetaMedicaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiRecetaMedicaApplication.class, args);
    }

}
