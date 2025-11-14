package cm.apiconsultorio.controller;

import cm.apiconsultorio.dto.ConsultorioRequest;
import cm.apiconsultorio.dto.ConsultorioResponse;
import cm.apiconsultorio.exceptions.ErrorResponse;
import cm.apiconsultorio.service.ConsultorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/consultorios")
@Validated
@RequiredArgsConstructor
public class ConsultorioController {

    private final ConsultorioService service;

    // ENDPOINTS CRUD

    @PostMapping
    @Operation(summary = "Registrar consultorio", description = "Registra un nuevo consultorio en la BD")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Consultorio registrado correctamente",
                    content = @Content(schema = @Schema(implementation = ConsultorioResponse.class))
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
    public ResponseEntity<ConsultorioResponse> registrar(
            @Parameter(description = "Datos requeridos para registrar el consultorio")
            @RequestBody
            @Valid
            ConsultorioRequest request) {

        log.info("Solicitud de registrar: {} recibida", request.nro_consultorio());
        ConsultorioResponse nuevoConsultorio = service.registrar(request);
        log.info("Solicitud de registrar: {} terminada, respuesta enviada", nuevoConsultorio.nro_consultorio());

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoConsultorio);
    }

    @GetMapping
    @Operation(summary = "Listar consultorios", description = "Lista todos los consultorios de la BD")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consultorios listados correctamente",
                    content = @Content(schema = @Schema(implementation = ConsultorioResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error Interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<ConsultorioResponse>> listar() {
        log.info("Solicitud de listar recibida");
        List<ConsultorioResponse> consultorios = service.listar();
        log.info("Solicitud de listar terminada, respuesta enviada");

        return ResponseEntity.ok(consultorios);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar consultorio",
            description = "Actualiza los datos de un consultorio existente")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consultorio actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = ConsultorioResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos o ID inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Consultorio no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ConsultorioResponse> actualizar(
            @Parameter(description = "Datos actualizados del consultorio")
            @RequestBody
            @Valid
            ConsultorioRequest request,

            @Parameter(description = "Identificador único del consultorio")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long id) {

        log.info("Solicitud de actualizar para ID: {} recibida", id);
        ConsultorioResponse consultorio = service.actualizar(request, id);
        log.info("Solicitud de actualizar para ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok(consultorio);
    }

    @DeleteMapping("/{nro_consultorio}")
    @Operation(summary = "Eliminar consultorio",
            description = "Elimina permanentemente un consultorio del sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Consultorio eliminado exitosamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "N° de Consultorio inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador único del consultorio")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            String nro_consultorio) {

        log.info("Solicitud de eliminar para N°: {} recibida", nro_consultorio);
        service.eliminar(nro_consultorio);
        log.info("Solicitud de eliminar para N°: {} terminada, respuesta enviada", nro_consultorio);

        return ResponseEntity.noContent().build();
    }

    // ENDPOINTS PARA BRINDAR DATOS A OTROS MICROSERVICIOS

    @GetMapping("/client/{nro_consultorio}")
    @Operation(summary = "Buscar consultorio", description = "Busca un consultorio por ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consultorio encontrado correctamente",
                    content = @Content(schema = @Schema(implementation = ConsultorioResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Consultorio no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "N° de Consultorio inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error Interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ConsultorioResponse> brindarConsultorio(
            @Parameter(description = "Identificador único del consultorio")
            @PathVariable
            String nro_consultorio) {

        log.info("Solicitud de búsqueda para N°: {} recibida", nro_consultorio);
        ConsultorioResponse consultorio = service.brindarConsultorio(nro_consultorio);
        log.info("Solicitud de búsqueda para N°: {} terminada, respuesta enviada", nro_consultorio);

        return ResponseEntity.ok(consultorio);
    }

}
