package com.CentroMedico.ApiHistoriaMedica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApiHistoriaMedicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiHistoriaMedicaApplication.class, args);
	}

}
