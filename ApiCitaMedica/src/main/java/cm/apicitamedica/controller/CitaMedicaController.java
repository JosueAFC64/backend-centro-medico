package cm.apicitamedica.controller;

import cm.apicitamedica.dto.CitaMedicaFeignResponse;
import cm.apicitamedica.dto.CitaMedicaRequest;
import cm.apicitamedica.dto.CitaMedicaResponse;
import cm.apicitamedica.exceptions.ErrorResponse;
import cm.apicitamedica.service.CitaMedicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/citas-medicas")
@RequiredArgsConstructor
@Validated
@Tag(name = "Citas Médicas", description = "API para gestión de citas médicas")
public class CitaMedicaController {

    private final CitaMedicaService service;

    // ENDPOINTS CRUD

    @PostMapping(produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Registrar nueva cita médica y genera un PDF de la misma",
            description = "Crea una nueva cita médica, genera un PDF de la misma y " +
                    "ocupa automáticamente un Slot de un Horario")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Cita médica creada exitosamente",
                    content = @Content(schema = @Schema(implementation = CitaMedicaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe una cita médica en ese slot",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<byte[]> registrar(
            @Parameter(description = "Datos requeridos para registrar/actualizar citas")
            @RequestBody
            @Valid
            CitaMedicaRequest request) {

        log.info("Solicitud de registrar cita para: {} recibida", request.dniPaciente());
        byte[] citaNueva = service.registrar(request);
        log.info("Solicitud de registrar cita para: {} terminada, respuesta enviada", request.dniPaciente());

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cita-medica.pdf")
                .body(citaNueva);
    }

    @GetMapping("/buscar/{idHorario}/{idDetalle}")
    @Operation(summary = "Buscar cita médica por idHorario e idDetalle",
            description = "Obtiene la cita médica de un slot y horario específicos")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cita médica obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = CitaMedicaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cita médica no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<CitaMedicaResponse> buscarPorHorarioSlot(
            @Parameter(description = "Identificador único del Horario")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long idHorario,

            @Parameter(description = "Identificador único del Slot del Horario")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long idDetalle) {

        log.info("Solicitud de buscar por idHorario: {} e idDetalle: {} recibida", idHorario, idDetalle);
        CitaMedicaResponse cita = service.buscarPorHorarioSlot(idHorario, idDetalle);
        log.info("Solicitud de buscar por idHorario: {} e idDetalle: {} terminada, respuesta enviada",
                idHorario, idDetalle);

        return ResponseEntity.ok().body(cita);
    }

    @GetMapping("/buscar/{dni}")
    @Operation(summary = "Buscar cita médica por DNI del Paciente",
            description = "Obtiene una cita médica específica por DNI del paciente")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cita médica obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = CitaMedicaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cita médica no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<CitaMedicaResponse>> buscarPorDniPaciente(
            @Parameter(description = "DNI único del Paciente")
            @PathVariable
            @NotNull(message = "El DNI es requerido")
            String dni) {

        log.info("Solicitud de buscar por DNI: {} recibida", dni);
        List<CitaMedicaResponse> citas = service.buscarPorDniPaciente(dni);
        log.info("Solicitud de buscar por DNI: {} terminada, respuesta enviada", dni);

        return ResponseEntity.ok().body(citas);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar cita médica",
            description = "Actualiza los datos de una cita médica existente")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cita médica actualizada correctamente",
                    content = @Content(schema = @Schema(implementation = CitaMedicaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos o ID inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cita médica no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<CitaMedicaResponse> actualizar (
            @Parameter(description = "Identificador único de la Cita")
            @PathVariable
            @Positive(message = "El ID de Cita debe ser positivo")
            Long id,

            @Parameter(description = "Datos actualizados de la Cita")
            @RequestBody
            @Valid
            CitaMedicaRequest request) {

        log.info("Solicitud de actualizar para cita con ID: {} recibida", id);
        CitaMedicaResponse cita = service.actualizar(request, id);
        log.info("Solicitud de actualizar para cita con ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok().body(cita);
    }

    @DeleteMapping("/{id}/{idHorario}/{idDetalle}")
    @Operation(summary = "Eliminar cita médica",
            description = "Elimina permanentemente una cita médica del sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cita médica eliminada exitosamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador único de la Cita")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long id,

            @Parameter(description = "Identificador único del Horario")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long idHorario,

            @Parameter(description = "Identificador único del Slot del Horario")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long idDetalle) {

        log.info("Solicitud de eliminar cita con ID: {} recibida", id);
        service.eliminar(id, idHorario, idDetalle);
        log.info("Solicitud de eliminar cita con ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/cancelar/{id}")
    @Operation(summary = "Cancela una cita médica",
            description = "Cancela una cita médica existente y libera el Slot asociado a ella")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cita cancelada correctamente",
                    content = @Content(schema = @Schema(implementation = CitaMedicaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Estado o ID inválido, Slot no disponible",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cita médica no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<CitaMedicaResponse> cancelarCita(
            @Parameter(description = "Identificador único de la Cita")
            @PathVariable
            @Positive(message = "El ID de la Cita debe ser positivo")
            Long id) {

        log.info("Solicitud de cancelar Cita con ID: {} recibida", id);
        CitaMedicaResponse cita = service.cancelarCita(id);
        log.info("Solicitud de cancelar Cita con ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok().body(cita);
    }

    @PutMapping("/completar/{id}")
    @Operation(summary = "Completar una cita médica",
            description = "Marca como completada una cita médica existente y registra un pago de la misma")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Cita marcada como completada correctamente",
                    content = @Content(schema = @Schema(implementation = CitaMedicaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Estado o ID inválido, Slot no disponible",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cita médica no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> completarCita(
            @Parameter(description = "Identificador único de la Cita")
            @PathVariable
            @Positive(message = "El ID de la Cita debe ser positivo")
            Long id) {

        log.info("Solicitud de completar Cita con ID: {} recibida", id);
        service.completarCita(id);
        log.info("Solicitud de completar Cita con ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.noContent().build();
    }

    // ENDPOINTS PARA BRINDAR DATOS A OTROS MICROSERVICIOS

    @GetMapping("/feign/{id}")
    @Operation(summary = "Buscar cita por su ID (de utilidad para otros microservicios)",
            description = "Obtiene una cita médica específica por su ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cita médica obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = CitaMedicaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cita médica no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<CitaMedicaFeignResponse> brindarCita(
            @Parameter(description = "Identificador único de la Cita")
            @PathVariable
            @Positive(message = "El ID de la Cita Médica debe ser positivo")
            Long id) {
        log.info("Solicitud de brindar Cita con ID: {} recibida", id);
        CitaMedicaFeignResponse cita = service.brindarCita(id);
        log.info("Solicitud de brindar Cita con ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok().body(cita);
    }
}
