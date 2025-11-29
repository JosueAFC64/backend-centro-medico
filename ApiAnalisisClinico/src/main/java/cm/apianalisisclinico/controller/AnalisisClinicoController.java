package cm.apianalisisclinico.controller;

import cm.apianalisisclinico.dto.analisisclinico.AnalisisClinicoRequest;
import cm.apianalisisclinico.dto.analisisclinico.AnalisisClinicoResponse;
import cm.apianalisisclinico.service.AnalisisClinicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/analisis")
@RequiredArgsConstructor
@Validated
@Tag(name = "Análisis Clínicos", description = "API para gestión de análisis clínicos")
public class AnalisisClinicoController {

    private final AnalisisClinicoService service;

    @PostMapping(produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Registrar Análisis Clínico")
    public ResponseEntity<byte[]> registrar(
            @Parameter(description = "Datos del nuevo Análisis Clínico")
            @RequestBody
            @Valid
            AnalisisClinicoRequest request) {

        log.info("Solicitud de registrar recibida");
        byte[] response = service.registrar(request);
        log.info("Solicitud de registrar terminada, respuesta enviada");

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orden-analisis.pdf")
                .body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar Análisis Clínico", description = "Busca un Análisis Clínico por ID")
    public ResponseEntity<AnalisisClinicoResponse> buscar(
            @Parameter(description = "Identificador único del Análisis Clínico")
            @Positive(message = "El ID debe ser positivo")
            @PathVariable
            Long id) {

        log.info("Solicitud de buscar para ID: {} recibida", id);
        AnalisisClinicoResponse response = service.buscar(id);
        log.info("Solicitud de buscar para ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/feign/{idAtencion}")
    @Operation(summary = "Brindar Análisis Clínico", description = "Busca un Análisis Clínico por idAtencion")
    public ResponseEntity<AnalisisClinicoResponse> brindarAnalisis(
            @Parameter(description = "Identificador único de la Atención Médica")
            @Positive(message = "El ID debe ser positivo")
            @PathVariable
            Long idAtencion) {

        log.info("Solicitud de buscar para ID Atención Médica: {} recibida", idAtencion);
        AnalisisClinicoResponse response = service.brindarAnalisis(idAtencion);
        log.info("Solicitud de buscar para ID Atención Médica: {} terminada, respuesta enviada", idAtencion);

        return ResponseEntity.ok().body(response);
    }

}
