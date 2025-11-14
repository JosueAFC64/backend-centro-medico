package cm.apiempleado.controller;

import cm.apiempleado.dto.request.EmpleadoRequest;
import cm.apiempleado.dto.response.*;
import cm.apiempleado.exceptions.ErrorResponse;
import cm.apiempleado.service.EmpleadoService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/empleados")
@RequiredArgsConstructor
@Validated
@Tag(name = "Empleados", description = "Operaciones CRUD y otros para empleados")
public class EmpleadoController {

    private final EmpleadoService service;

    // ENDPOINTS CRUD

    @PostMapping
    @Operation(summary = "Registrar empleado", description = "Registra un nuevo empleado en el sistema")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Empleado registrado exitosamente",
                    content = @Content(schema = @Schema(implementation = EmpleadoResponse.class))
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
    public ResponseEntity<EmpleadoResponse> registrar(
            @Parameter(description = "Datos del nuevo empleado")
            @RequestBody
            @Valid
            EmpleadoRequest request) {

        log.info("Solicitud de registro recibida: {}", request.correo());
        EmpleadoResponse nuevoEmpleado = service.registrar(request);
        log.info("Solicitud de registro: {} terminada, respuesta enviada", request.correo());

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado);
    }

    @GetMapping
    @Operation(summary = "Listar empleados", description = "Lista todos los empleados activos")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de empleados obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = EmpleadoSumResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<EmpleadoSumResponse>> listar() {

        log.info("Solicitud de listar recibida");
        List<EmpleadoSumResponse> empleados = service.listar();
        log.info("Solicitud de listar terminada, respuesta enviada");

        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar empleado por ID",
            description = "Obtiene la información completa de un empleado específico")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Empleado encontrado exitosamente",
                    content = @Content(schema = @Schema(implementation = EmpleadoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<EmpleadoResponse> buscar(
            @Parameter(description = "Identificador único del empleado", example = "1")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long id) {

        log.info("Solicitud de búsqueda con ID: {} recibida", id);
        EmpleadoResponse empleado = service.buscar(id);
        log.info("Solicitud de búsqueda con ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok(empleado);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar empleado", description = "Actualiza la información de un empleado existente")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Empleado actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = EmpleadoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos o ID inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<EmpleadoResponse> actualizar(
            @Parameter(description = "Datos actualizados del empleado")
            @RequestBody
            @Valid
            EmpleadoRequest request,

            @Parameter(description = "Identificador único del empleado", example = "1")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long id) {

        log.info("Solicitud de actualización con ID: {} recibida", id);
        EmpleadoResponse empleado = service.actualizar(request, id);
        log.info("Solicitud de actualización con ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok(empleado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar empleado", description = "Elimina permanentemente un empleado del sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Empleado eliminado exitosamente"),
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
            @Parameter(description = "Identificador único del empleado", example = "1")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long id) {

        log.info("Solicitud de eliminación con ID: {} recibida", id);
        service.eliminar(id);
        log.info("Solicitud de eliminación con ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.noContent().build();
    }

    // OTROS ENDPOINTS

    @GetMapping("/filtrar/especialidad")
    @Operation(summary = "Filtrar médicos por especialidad",
            description = "Obtiene todos los médicos activos que pertenecen a una especialidad específica")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de médicos obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = MedicoPorEspecialidadResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID de especialidad inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<MedicoPorEspecialidadResponse> filtrarPorEspecialidad(
            @Parameter(description = "Identificador único de la especialidad", example = "1")
            @RequestParam
            @NotNull(message = "El ID no debe ser nulo")
            @Positive(message = "El ID debe ser positivo")
            Long especialidadId) {

        log.info("Solicitud de filtrar por especialidad con ID: {} recibida", especialidadId);
        MedicoPorEspecialidadResponse medicos = service.filtrarPorEspecialidad(especialidadId);
        log.info("Solicitud de filtrar por especialidad con ID: {} terminada, respuesta enviada", especialidadId);

        return ResponseEntity.ok(medicos);
    }

    @GetMapping("/medicos")
    public ResponseEntity<List<MedicoResponse>> listarMedicos() {
        log.info("Solicitud de listar médicos recibida");
        List<MedicoResponse> medicos = service.listarMedicos();
        log.info("Solicitud de listar médicos terminada, respuesta enviada");

        return ResponseEntity.ok().body(medicos);
    }

    // ENDPOINTS PARA BRINDAR DATOS A OTROS MICROSERVICIOS

    @GetMapping("/client/{id}")
    @Operation(summary = "Buscar el nombre de un empleado por ID",
            description = "Obtiene el ID y nombre de un empleado específico (PARA OTROS MICROSERVICIOS)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Empleado encontrado exitosamente",
                    content = @Content(schema = @Schema(implementation = EmpleadoClientResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<EmpleadoClientResponse> brindarNombre(
            @Parameter(description = "Identificador único del empleado", example = "1")
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long id) {

        log.info("Solicitud de buscar nombre de empleado con ID: {} recibida", id);
        EmpleadoClientResponse empleado = service.brindarNombre(id);
        log.info("Solicitud de buscar nombre de empleado con ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok(empleado);
    }

}
