package com.CentroMedico.ApiPaciente;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApiPacienteApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiPacienteApplication.class, args);
	}

}
