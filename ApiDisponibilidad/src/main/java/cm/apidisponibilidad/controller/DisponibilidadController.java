package cm.apidisponibilidad.controller;

import cm.apidisponibilidad.dto.DisponibilidadRequest;
import cm.apidisponibilidad.dto.DisponibilidadResponse;
import cm.apidisponibilidad.exceptions.ErrorResponse;
import cm.apidisponibilidad.service.DisponibilidadService;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/disponibilidades")
@Validated
@RequiredArgsConstructor
@Tag(name = "Disponibilidades", description = "Operaciones CRUD y otros para disponibilidades")
public class DisponibilidadController {

    private final DisponibilidadService service;

    // ENDPOINTS CRUD

    @PostMapping
    @Operation(summary = "Registrar disponibilidad", description = "Registra la disponibilidad de un médico")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Disponibilidad registrada exitosamente",
                    content = @Content(schema = @Schema(implementation = DisponibilidadResponse.class))
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
    public ResponseEntity<DisponibilidadResponse> registrar(
            @Parameter(description = "Datos para registrar la disponibilidad")
            @RequestBody
            @Valid
            DisponibilidadRequest request) {

        log.info("Solicitud de registro con ID de médico: {} recibida", request.idMedico());
        DisponibilidadResponse nuevaDisponibilidad = service.registrar(request);
        log.info("Solicitud de registro con ID de médico: {} terminada, respuesta enviada", request.idMedico());

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaDisponibilidad);
    }

    @GetMapping
    @Operation(summary = "Listar disponibilidades", description = "Lista todas las disponibilidades")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de disponibilidades obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = DisponibilidadResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<DisponibilidadResponse>> listar() {

        log.info("Solicitud de listar recibida");
        List<DisponibilidadResponse> disponibilidades = service.listar();
        log.info("Solicitud de listar terminada, respuesta enviada");

        return ResponseEntity.ok(disponibilidades);
    }

    @GetMapping("/{idMedico}")
    @Operation(summary = "Buscar todas las disponibilidades por médico",
            description = "Busca todas las disponibilidades de un médico por su ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de disponibilidades de médico obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = DisponibilidadResponse.class))
            ),
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
    public ResponseEntity<List<DisponibilidadResponse>> buscarTodosPorMedico(
            @Parameter(description = "Identificador único del médico", example = "1")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long idMedico) {

        log.info("Solicitud de buscar todas las disponibilidades por idMedico: {} recibida", idMedico);
        List<DisponibilidadResponse> disponibilidadesMedico = service.buscarTodosPorMedico(idMedico);
        log.info(
                "Solicitud de buscar todas las disponibilidades por idMedico: {} terminada, respuesta enviada",
                idMedico
        );

        return ResponseEntity.ok(disponibilidadesMedico);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar disponibilidad",
            description = "Actualiza los datos de una disponibilidad existente")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Disponibilidad actualizada correctamente",
                    content = @Content(schema = @Schema(implementation = DisponibilidadResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos o ID inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Disponibilidad no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<DisponibilidadResponse> actualizar(
            @Parameter(description = "Datos actualizados del empleado")
            @RequestBody
            @Valid
            DisponibilidadRequest request,

            @Parameter(description = "Identificador único de la disponibilidad", example = "1")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long id) {

        log.info("Solicitud de actualización con ID: {} recibida", id);
        DisponibilidadResponse disponibilidad = service.actualizar(request, id);
        log.info("Solicitud de actualización con ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok(disponibilidad);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar disponibilidad",
            description = "Elimina permanentemente una disponibilidad del sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Disponibilidad eliminada exitosamente"),
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
            @Parameter(description = "Identificador único de la disponibilidad")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long id) {

        log.info("Solicitud de eliminación con ID: {} recibida", id);
        service.eliminar(id);
        log.info("Solicitud de eliminación con ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.noContent().build();
    }
}
