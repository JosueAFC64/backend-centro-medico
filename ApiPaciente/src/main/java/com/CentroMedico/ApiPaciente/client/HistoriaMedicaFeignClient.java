package com.CentroMedico.ApiPaciente.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ApiHistoriaMedica")
public interface HistoriaMedicaFeignClient {

    @CircuitBreaker(name = "ApiHistoriaMedica", fallbackMethod = "cascadaConsultaFallida")
    @GetMapping("/historias/existe/paciente/{idPaciente}")
    boolean existeHistoriaPorIdPaciente(@PathVariable Long idPaciente);


    default boolean cascadaConsultaFallida(Long idPaciente, Throwable t) {
        System.err.println("CRÍTICO: Falló la consulta de existencia de Historia Médica para ID: " + idPaciente + ". Bloqueando la eliminación por seguridad.");
        return true;
    }
}


