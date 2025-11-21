package com.CentroMedico.ApiHistoriaMedica.client.paciente;

import com.CentroMedico.ApiHistoriaMedica.dto.PacienteSimpleResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.Collections;

import java.util.List;

@FeignClient(name = "ApiPaciente")
public interface PacienteFeignClient {

    /**
     * buscarPacientePorDni
     */
    @CircuitBreaker(name = "ApiPaciente", fallbackMethod = "obtenerPacienteAlternativo")
    @GetMapping("/pacientes/simple/dni/{dni}") //
    PacienteSimpleResponse buscarPacientePorDni(@PathVariable String dni);


    /**
     * Metodo Fallback (Alternativo) que se ejecuta cuando el CircuitBreaker se abre o falla.
     */
    default PacienteSimpleResponse obtenerPacienteAlternativo(String dni, Throwable t) {
        return PacienteSimpleResponse.builder()
                .dni(dni)
                .nombres("Servicio No Disponible")
                .apellidos("Servicio No Disponible")
                .idPaciente(0L)
                .fechaNacimiento(LocalDate.of(1900, 1, 1)) // fecha centinela
                .build();
    }

    /**
     * buscarPacientesPorNombre
     */

    @CircuitBreaker(name = "ApiPaciente", fallbackMethod = "obtenerPacientesPorNombreAlternativo")
    @GetMapping("/pacientes/buscar/nombre/{nombre}")
    List<PacienteSimpleResponse> buscarPacientesPorNombre(@PathVariable String nombre);

    /**
     * Metodo Fallback para la búsqueda por nombre. Devuelve una lista vacía.
     */
    default List<PacienteSimpleResponse> obtenerPacientesPorNombreAlternativo(String nombre, Throwable t) {
        return Collections.emptyList();
    }
}
