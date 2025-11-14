package cm.apicitamedica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApiCitaMedicaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiCitaMedicaApplication.class, args);
    }

}
