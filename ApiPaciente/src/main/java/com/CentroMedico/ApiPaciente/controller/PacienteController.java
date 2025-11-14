package com.CentroMedico.ApiPaciente.controller;

import com.CentroMedico.ApiPaciente.dto.*;
import com.CentroMedico.ApiPaciente.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes")
@RequiredArgsConstructor

public class PacienteController {
    private final PacienteService service;

    /**Registra un nuevo paciente**/

    @PostMapping("/registrar")
    public ResponseEntity<PacienteResponse> registrar(@Valid @RequestBody PacienteRequest request) {
        PacienteResponse response = service.registrar(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**Lista todos los pacientes**/
    @GetMapping("/listar")
    public ResponseEntity<List<PacienteSumResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    /**Actualizar un paciente por su ID**/
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<PacienteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequest request) {
        PacienteResponse response = service.actualizar(id, request);
        return ResponseEntity.ok(response);
    }

    /**Eliminar un paciente por su ID**/
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**Busca un paciente por su ID**/
    @GetMapping("/buscar/{id}")
    public ResponseEntity<PacienteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**Busca un paciente por su DNI. (DTO completo) **/
    @GetMapping("/buscar/dni/{dni}")
    public ResponseEntity<PacienteResponse> buscarPorDni(@PathVariable String dni) { // <<<< CAMBIADO: A PacienteResponse
        return ResponseEntity.ok(service.buscarPorDni(dni));
    }

    /**
     * Busca pacientes por nombre o apellido. (DTO completo)
     **/
    @GetMapping("/buscar/nombre/{nombre}")
    public ResponseEntity<List<PacienteResponse>> buscarPorNombre(@PathVariable String nombre) { // <<<< CAMBIADO: A List<PacienteResponse>
        List<PacienteResponse> response = service.buscarPorNombre(nombre);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca un paciente por su DNI (Versión Simple para uso unico en Microservicio ApiHistoriaMedica).
     **/
    @GetMapping("/simple/dni/{dni}")
    public ResponseEntity<PacienteSimpleResponse> buscarPorDniSimple(@PathVariable String dni) {
        return ResponseEntity.ok(service.buscarPorDniSimple(dni));
    }

    /**
     * Busca un paciente por su DNI (Versión Simple para uso unico en Microservicio ApiPago).
     **/
    @GetMapping("/feign/{dni}")
    public ResponseEntity<PacienteClientResponse> brindarDatosSimples(@PathVariable String dni) {
        return ResponseEntity.ok(service.brindarDatosSimples(dni));
    }

}
