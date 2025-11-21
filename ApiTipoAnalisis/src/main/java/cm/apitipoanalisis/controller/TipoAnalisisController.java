package cm.apitipoanalisis.controller;

import cm.apitipoanalisis.dto.TipoAnalisisRequest;
import cm.apitipoanalisis.dto.TipoAnalisisResponse;
import cm.apitipoanalisis.service.TipoAnalisisService;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tipo-analisis")
@RequiredArgsConstructor
@Validated
@Tag(name = "Tipo de Análisis", description = "API para gestión de tipos de análisis")
public class TipoAnalisisController {

    private final TipoAnalisisService service;

    @PostMapping
    @Operation(summary = "Registrar Tipo Análisis", description = "Registra un nuevo Tipo de Análisis")
    public ResponseEntity<TipoAnalisisResponse> registrar(
            @Parameter(description = "Datos del nuevo Tipo de Análisis")
            @RequestBody
            @Valid
            TipoAnalisisRequest request) {

        log.info("Solicitud de registrar para: {} recibida", request.nombre());
        TipoAnalisisResponse ta = service.registrar(request);
        log.info("Solicitud de registrar para: {} terminada, respuesta enviada", request.nombre());

        return ResponseEntity.status(HttpStatus.CREATED).body(ta);
    }

    @GetMapping
    @Operation(summary = "Listar Tipos Análisis", description = "Lista todos los Tipos de Análisis")
    public ResponseEntity<List<TipoAnalisisResponse>> listar() {
        log.info("Solicitud de listar recibida");
        List<TipoAnalisisResponse> tas = service.listar();
        log.info("Solicitud de listar terminada, respuesta enviada");

        return ResponseEntity.ok().body(tas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar Tipo Análisis", description = "Busca un Tipo de Análisis por su ID")
    public ResponseEntity<TipoAnalisisResponse> buscar(
            @Parameter(description = "Identificador único del Tipo de Análisis")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long id) {

        log.info("Solicitud de buscar para: {} recibida", id);
        TipoAnalisisResponse ta = service.buscar(id);
        log.info("Solicitud de buscar para: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok().body(ta);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar Tipo Análisis", description = "Actualiza los datos de un Tipo de Análisis")
    public ResponseEntity<TipoAnalisisResponse> actualizar(
            @Parameter(description = "Datos del nuevo Tipo de Análisis")
            @RequestBody
            @Valid
            TipoAnalisisRequest request,

            @Parameter(description = "Identificador único del Tipo de Análisis")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long id) {

        log.info("Solicitud de actualizar para: {} recibida", id);
        TipoAnalisisResponse ta = service.actualizar(request, id);
        log.info("Solicitud de actualizar para: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok().body(ta);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Tipo Análisis", description = "Elimina un Tipo de Análisis por su ID")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador único del Tipo de Análisis")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long id) {

        log.info("Solicitud de eliminar para: {} recibida", id);
        service.eliminar(id);
        log.info("Solicitud de eliminar para: {} terminada, respuesta enviada", id);

        return ResponseEntity.noContent().build();
    }

}
