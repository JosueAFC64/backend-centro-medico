package com.CentroMedico.ApiHistoriaMedica.controller;

import com.CentroMedico.ApiHistoriaMedica.dto.HistoriaMedicaRequest;
import com.CentroMedico.ApiHistoriaMedica.dto.HistoriaMedicaResponse;
import com.CentroMedico.ApiHistoriaMedica.service.HistoriaMedicaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historias")
@RequiredArgsConstructor
public class HistoriaMedicaController {
    private final HistoriaMedicaService service;

    /**
     * Registra una nueva historia médica.
     * Requiere el DNI del paciente para validación y persistencia.
     **/
    @PostMapping("/registrar")
    public ResponseEntity<HistoriaMedicaResponse> registrar(@Valid @RequestBody HistoriaMedicaRequest request) {
        HistoriaMedicaResponse response = service.registrar(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Busca una historia médica por su ID (que es el DNI del paciente).
     * La respuesta incluye datos del paciente obtenidos de ApiPaciente via Feign.
     **/
    @GetMapping("/buscar/{dni}")
    public ResponseEntity<HistoriaMedicaResponse> buscarPorDni(@PathVariable String dni) {
        HistoriaMedicaResponse response = service.buscarPorDni(dni);
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza una historia médica por su ID (DNI en este caso).
     **/
    @PutMapping("/actualizar/{dni}")
    public ResponseEntity<HistoriaMedicaResponse> actualizar(
            @PathVariable String dni,
            @Valid @RequestBody HistoriaMedicaRequest request) {
        HistoriaMedicaResponse response = service.actualizar(dni, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/feign/{dniPaciente}/{idAtencion}")
    public ResponseEntity<Void> registrarAtencionMedica(
            @PathVariable String dniPaciente,
            @PathVariable Long idAtencion) {

        service.registrarAtencionMedica(dniPaciente, idAtencion);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca una lista de historias médicas buscando pacientes por nombre o apellido
     * en ApiPaciente, y luego buscando las historias asociadas.
     **/
    @GetMapping("/buscar/nombre/{nombre}")
    public ResponseEntity<List<HistoriaMedicaResponse>> buscarPorNombrePaciente(@PathVariable String nombre) {
        List<HistoriaMedicaResponse> response = service.buscarPorNombrePaciente(nombre);
        return ResponseEntity.ok(response);
    }

    /**
     * Metodo para ApiPaciente que valida si ya existe una historia medica para un paciente.
     **/
    @GetMapping("/existe/paciente/{idPaciente}")
    public ResponseEntity<Boolean> existeHistoriaPorIdPaciente(@PathVariable Long idPaciente) {
        boolean existe = service.existeHistoriaPorIdPaciente(idPaciente);
        return ResponseEntity.ok(existe);
    }
}
