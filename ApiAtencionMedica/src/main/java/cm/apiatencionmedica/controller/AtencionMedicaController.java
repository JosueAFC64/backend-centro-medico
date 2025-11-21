package cm.apiatencionmedica.controller;

import cm.apiatencionmedica.dto.AtencionMedicaFeignResponse;
import cm.apiatencionmedica.dto.AtencionMedicaRequest;
import cm.apiatencionmedica.dto.AtencionMedicaResponse;
import cm.apiatencionmedica.exceptions.ErrorResponse;
import cm.apiatencionmedica.service.AtencionMedicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/atenciones-medicas")
@RequiredArgsConstructor
@Validated
@Tag(name = "Atenciones Médicas", description = "Operaciones CRUD y otros para atenciones médicas")
public class AtencionMedicaController {

    private final AtencionMedicaService service;

    @PostMapping
    @Operation(summary = "Registrar atención médica", description = "Registra una nueva atención médica en el sistema")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Atención médica registrado exitosamente",
                    content = @Content(schema = @Schema(implementation = AtencionMedicaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o duplicados",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error Interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<AtencionMedicaResponse> registrar(
            @Parameter(description = "Datos de la Atención Médica")
            @RequestBody
            @Valid
            AtencionMedicaRequest request) {

        log.info("Solicitud de registrar recibida para ID: {}", request.idCita());
        AtencionMedicaResponse atencion = service.registrar(request);
        log.info("Solicitud de registrar para ID: {} terminada, respuesta enviada", request.idCita());

        return ResponseEntity.status(HttpStatus.CREATED).body(atencion);
    }

    @GetMapping("/{idCita}")
    @Operation(summary = "Buscar atención médica por idCita", description = "Busca una atención médica por ID de Cita Médica")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Atención médica brindada correctamente",
                    content = @Content(schema = @Schema(implementation = AtencionMedicaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Atención médica no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<AtencionMedicaResponse> buscarPorIdCita(
            @Parameter(description = "Identificador único de la Cita Médica")
            @Positive(message = "El ID de la Cita Médica debe ser positivo")
            @PathVariable
            Long idCita) {

        log.info("Solicitud de buscar por ID Cita: {} recibida", idCita);
        AtencionMedicaResponse atencion = service.buscarPorIdCita(idCita);
        log.info("Solicitud de buscar por ID Cita: {} terminada, respuesta enviada", idCita);

        return ResponseEntity.ok().body(atencion);
    }

    // ENDPOINTS PARA BRINDAR DATOS A OTROS MICROSERVICIOS

    @GetMapping("/feign/{id}")
    @Operation(summary = "Brindar atención médica", description = "Brinda datos de una atención médica a otros microservicios")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Atención médica brindada correctamente",
                    content = @Content(schema = @Schema(implementation = AtencionMedicaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Atención médica no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<AtencionMedicaFeignResponse> brindarAtencionMedica(
            @Parameter(description = "Identificador único de la Atención Médica")
            @Positive(message = "El ID de la Atención Médica debe ser positivo")
            @PathVariable
            Long id
    ) {
        log.info("Solicitud de brindar para ID: {} recibida ", id);
        AtencionMedicaFeignResponse atencion = service.brindarAtencionMedica(id);
        log.info("Solicitud de brindar para ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok().body(atencion);
    }

}
