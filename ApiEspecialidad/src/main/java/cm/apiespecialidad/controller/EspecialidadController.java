package cm.apiespecialidad.controller;

import cm.apiespecialidad.dto.EspecialidadRequest;
import cm.apiespecialidad.dto.EspecialidadResponse;
import cm.apiespecialidad.exceptions.ErrorResponse;
import cm.apiespecialidad.service.EspecialidadService;
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
@RequestMapping("/especialidades")
@RequiredArgsConstructor
@Validated
@Tag(name = "Especialidad", description = "Operaciones CRUD para especialidades")
public class EspecialidadController {

    private final EspecialidadService service;

    // ENDPOINTS CRUD

    @PostMapping
    @Operation(summary = "Registrar especialidad", description = "Registra una nueva especialidad en el sistema")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Especialidad registrada exitosamente",
                    content = @Content(schema = @Schema(implementation = EspecialidadResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o duplicados",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<EspecialidadResponse> registrar(
            @Parameter(description = "Datos de la nueva especialidad")
            @Valid
            @RequestBody
            EspecialidadRequest request){

        log.info("Solicitud de registro recibida: {}", request.nombre());
        EspecialidadResponse nuevaEspecialidad = service.registrar(request);
        log.info("Solicitud de registro: {} terminada, respuesta enviada", request.nombre());

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaEspecialidad);
    }

    @GetMapping
    @Operation(summary = "Listar especialidades", description = "Lista todas las especialidades")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de especialidades obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = EspecialidadResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<EspecialidadResponse>> listar(){

        log.info("Solicitud de listar recibida");
        List<EspecialidadResponse> especialidades = service.listar();
        log.info("Solicitud de listar terminada, respuesta enviada");

        return ResponseEntity.ok(especialidades);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar especialidad por ID",
            description = "Obtiene la información completa de una especialidad en específico")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Especialidad encontrada exitosamente",
                    content = @Content(schema = @Schema(implementation = EspecialidadResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Especialidad no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<EspecialidadResponse> buscar(
            @Parameter(description = "Identificador único de la especialidad", example = "1")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long id){

        log.info("Solicitud de búsqueda con ID: {} recibida", id);
        EspecialidadResponse especialidad = service.buscar(id);
        log.info("Solicitud de búsqueda con ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok(especialidad);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar especialidad",
            description = "Actualiza la información de una especialidad existente")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Especialidad actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = EspecialidadResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request nulo, ID o datos inválidos, Datos duplicados",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Especialidad no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<EspecialidadResponse> actualizar(
            @Parameter(description = "Información actualizada de la especialidad")
            @Valid
            @RequestBody
            EspecialidadRequest request,

            @Parameter(description = "Identificador único de la especialidad", example = "1")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long id){

        log.info("Solicitud de actualización con ID: {} recibida", id);
        EspecialidadResponse especialidad = service.actualizar(request, id);
        log.info("Solicitud de actualización con ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok(especialidad);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar especialidad", description = "Elimina permanentemente una especialidad del sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Especialidad eliminada exitosamente"),
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
            @Parameter(description = "Identificador único de la especialidad", example = "1")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long id) {

        log.info("Solicitud de eliminación con ID: {} recibida", id);
        service.eliminar(id);
        log.info("Solicitud de eliminación con ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.noContent().build();
    }
}
