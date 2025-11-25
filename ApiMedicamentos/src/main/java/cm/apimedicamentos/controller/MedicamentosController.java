package cm.apimedicamentos.controller;

import cm.apimedicamentos.dto.MedicamentosRequest;
import cm.apimedicamentos.dto.MedicamentosResponse;
import cm.apimedicamentos.service.MedicamentosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/medicamentos")
@RequiredArgsConstructor
public class MedicamentosController {

    private final MedicamentosService service;

    @PostMapping
    @Operation(summary = "Registrar Medicamento", description = "Registra un nuevo Medicamento")
    public ResponseEntity<MedicamentosResponse> registrar(
            @Parameter(description = "Datos del nuevo Medicamento")
            @RequestBody
            @Valid
            MedicamentosRequest request) {

        log.info("Solicitud de registrar: {} recibida", request.nombre());
        MedicamentosResponse response = service.registrar(request);
        log.info("Solicitud de registrar: {} terminada, respuesta enviada", request.nombre());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar Medicamentos", description = "Lista todos los Medicamentos existentes")
    public ResponseEntity<List<MedicamentosResponse>> listar() {
        log.info("Solicitud de listar recibida");
        List<MedicamentosResponse> response = service.listar();
        log.info("Solicitud de listar recibida, respuesta enviada");

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar Medicamento", description = "Busca un Medicamento por ID")
    public ResponseEntity<MedicamentosResponse> buscar(
            @Parameter(description = "Identificador único del Medicamento")
            @Positive(message = "El ID debe ser positivo")
            @PathVariable
            Long id) {

        log.info("Solicitud de buscar para ID: {} recibida", id);
        MedicamentosResponse response = service.buscar(id);
        log.info("Solicitud de buscar para ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Medicamento", description = "Elimina un Medicamento permanentemente")
    public ResponseEntity<MedicamentosResponse> eliminar(
            @Parameter(description = "Identificador único del Medicamento")
            @Positive(message = "El ID debe ser positivo")
            @PathVariable
            Long id) {

        log.info("Solicitud de eliminar para ID: {} recibida", id);
        service.eliminar(id);
        log.info("Solicitud de eliminar para ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.noContent().build();
    }

}
