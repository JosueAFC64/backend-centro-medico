package cm.apirecetamedica.controller;

import cm.apirecetamedica.dto.recetamedica.RecetaMedicaRequest;
import cm.apirecetamedica.dto.recetamedica.RecetaMedicaResponse;
import cm.apirecetamedica.service.RecetaMedicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/recetas")
@RequiredArgsConstructor
@Validated
@Tag(name = "Recetas Médicas", description = "API para gestión de recetas médicas")
public class RecetaMedicaController {

    private final RecetaMedicaService service;

    @PostMapping
    @Operation(summary = "Registrar Receta Médica", description = "Registra una nueva Receta Médica")
    public ResponseEntity<RecetaMedicaResponse> registrar(
            @Parameter(description = "Datos de la nueva Receta Médica")
            @RequestBody
            @Valid
            RecetaMedicaRequest request) {

        log.info("Solicitud de registrar recibida");
        RecetaMedicaResponse response = service.registrar(request);
        log.info("Solicitud de registrar terminada, respuesta enviada");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar Receta Médica", description = "Busca una Receta Médica por ID")
    public ResponseEntity<RecetaMedicaResponse> buscar(
            @Parameter(description = "Identificador único de la Receta Médica")
            @Positive(message = "El ID debe ser positivo")
            @PathVariable
            Long id) {

        log.info("Solicitud de buscar con ID: {} recibida", id);
        RecetaMedicaResponse response = service.buscar(id);
        log.info("Solicitud de buscar con ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok().body(response);
    }

}
