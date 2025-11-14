package cm.apihorario.controller;

import cm.apihorario.dto.HorarioRequest;
import cm.apihorario.dto.HorarioResponse;
import cm.apihorario.dto.SlotClientResponse;
import cm.apihorario.exceptions.ErrorResponse;
import cm.apihorario.service.HorarioService;
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

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/horarios")
@RequiredArgsConstructor
@Validated
@Tag(name = "Horarios", description = "API para gestión de horarios médicos")
public class HorarioController {

    private final HorarioService horarioService;

    @PostMapping
    @Operation(summary = "Registrar nuevo horario", 
               description = "Crea un nuevo horario con slots automáticamente generados")
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "201",
                description = "Horario creado exitosamente",
                content = @Content(schema = @Schema(implementation = HorarioResponse.class))
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Datos de entrada inválidos",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Ya existe un horario para el empleado en esa fecha",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<HorarioResponse> registrarHorario(
            @Parameter(description = "Datos del nuevo horario")
            @RequestBody
            @Valid
            HorarioRequest request) {

        log.info("Solicitud de registro recibida para ID médico: {}", request.idEmpleado());
        HorarioResponse response = horarioService.registrar(request);
        log.info("Solicitud de registro para ID médico: {} terminada, respuesta enviada", request.idEmpleado());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar horarios", description = "Lista todos los horarios de la base de datos")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Lista de horarios obtenida correctamente",
                content = @Content(schema = @Schema(implementation = HorarioResponse.class))
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Error interno del servidor",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<List<HorarioResponse>> listar() {

        log.info("Solicitud de listar recibida");
        List<HorarioResponse> horarios = horarioService.listar();
        log.info("Solicitud de listar terminada, respuesta enviada");

        return ResponseEntity.ok(horarios);
    }

    @GetMapping("/empleado/{idEmpleado}/fecha/{fecha}")
    @Operation(summary = "Buscar horarios por empleado y fecha",
               description = "Obtiene todos los horarios de un empleado en una fecha específica")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de horarios obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = HorarioResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<HorarioResponse>> buscarPorEmpleadoYFecha(
            @Parameter(description = "Identificador único del empleado")
            @PathVariable
            @Positive(message = "El ID de Empleado debe ser positivo")
            Long idEmpleado,

            @Parameter(description = "Fecha del/los horarios")
            @PathVariable
            LocalDate fecha) {

        log.info("Solicitud de búsqueda por ID médico: {} y fecha: {} recibida", idEmpleado, fecha);
        List<HorarioResponse> horarios = horarioService.buscarPorEmpleadoYFecha(idEmpleado, fecha);
        log.info("Solicitud de búsqueda por ID médico: {} y fecha: {} terminada, respuesta enviada", idEmpleado, fecha);

        return ResponseEntity.ok(horarios);
    }

    @PutMapping("/{idHorario}/slots/{idDetalle}/ocupar")
    @Operation(summary = "Ocupar slot", 
               description = "Marca un slot como ocupado para una cita")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Slot ocupado exitosamente"),
        @ApiResponse(
                responseCode = "400",
                description = "Slot no disponible o datos inválidos",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Horario o detalle no encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Void> ocuparSlot(
            @Parameter(description = "Identificador único del horario")
            @PathVariable
            @Positive(message = "El ID de Horario debe ser positivo")
            Long idHorario,

            @Parameter(description = "Identificador único del detalle/slot")
            @PathVariable
            @Positive(message = "El ID de Detalle debe ser positivo")
            Long idDetalle,

            @Parameter(description = "Identificador único de la cita")
            @RequestParam
            @NotNull(message = "El ID de Cita es requerido")
            @Positive(message = "El ID de Cita debe ser positivo")
            Long idCita) {

        log.info("Solicitud de ocupar slot: {} recibida", idDetalle);
        horarioService.ocuparSlot(idHorario, idDetalle, idCita);
        log.info("Solicitud de ocupar slot: {} terminada, respuesta enviada", idDetalle);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{idHorario}/slots/{idDetalle}/liberar")
    @Operation(summary = "Liberar slot", 
               description = "Libera un slot ocupado (cancela una cita)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Slot liberado exitosamente"),
        @ApiResponse(
                responseCode = "400",
                description = "Slot no ocupado o datos inválidos",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Horario o detalle no encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Void> liberarSlot(
            @Parameter(description = "Identificador único del horario")
            @PathVariable
            @Positive(message = "El ID de Horario debe ser positivo")
            Long idHorario,

            @Parameter(description = "Identificador único del detalle/slot")
            @PathVariable
            @Positive(message = "El ID de Detalle debe ser positivo")
            Long idDetalle) {

        log.info("Solicitud de liberar slot: {} recibida", idDetalle);
        horarioService.liberarSlot(idHorario, idDetalle);
        log.info("Solicitud de liberar slot: {} terminada, respuesta enviada", idDetalle);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{idHorario}/slots/{idDetalle}/bloquear")
    @Operation(summary = "Bloquear slot", 
               description = "Bloquea un slot para evitar reservas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Slot bloqueado exitosamente"),
        @ApiResponse(
                responseCode = "400",
                description = "Slot no disponible para bloqueo",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Horario o detalle no encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Void> bloquearSlot(
            @Parameter(description = "Identificador único del horario")
            @PathVariable
            @Positive(message = "El ID de Horario debe ser positivo")
            Long idHorario,

            @Parameter(description = "Identificador único del detalle/slot")
            @PathVariable
            @Positive(message = "El ID de Detalle debe ser positivo")
            Long idDetalle) {

        log.info("Solicitud de bloquear slot: {} recibida", idDetalle);
        horarioService.bloquearSlot(idHorario, idDetalle);
        log.info("Solicitud de bloquear slot: {} terminada, respuesta enviada", idDetalle);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{idHorario}/slots/{idDetalle}/desbloquear")
    @Operation(summary = "Desbloquear slot",
            description = "Desbloquea un slot para admitir reservas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Slot desbloqueado exitosamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Slot no disponible para desbloqueo",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Horario o detalle no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> desbloquearSlot(
            @Parameter(description = "Identificador único del horario")
            @PathVariable
            @Positive(message = "El ID de Horario debe ser positivo")
            Long idHorario,

            @Parameter(description = "Identificador único del detalle/slot")
            @PathVariable
            @Positive(message = "El ID de Detalle debe ser positivo")
            Long idDetalle) {

        log.info("Solicitud de desbloquear slot: {} recibida", idDetalle);
        horarioService.desbloquearSlot(idHorario, idDetalle);
        log.info("Solicitud de desbloquear slot: {} terminada, respuesta enviada", idDetalle);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar horario", 
               description = "Elimina un horario y todos sus slots")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horario eliminado exitosamente"),
        @ApiResponse(
                responseCode = "400",
                description = "ID inválido",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Horario no encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Void> eliminarHorario(
            @Parameter(description = "Identificador único del horario")
            @PathVariable
            @Positive(message = "El ID de Horario debe ser positivo")
            Long id) {

        log.info("Solicitud de eliminar horario: {} recibida", id);
        horarioService.eliminar(id);
        log.info("Solicitud de eliminar horario: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok().build();
    }

    // ENDPOINTS PARA BRINDAR DATOS A OTROS MICROSERVICIOS

    @GetMapping("/client/{idHorario}/slots/{idDetalle}")
    public ResponseEntity<SlotClientResponse> brindarSlot(
            @Parameter(description = "Identificador único del horario")
            @PathVariable
            @Positive(message = "El ID de Horario debe ser positivo")
            Long idHorario,

            @Parameter(description = "Identificador único del detalle/slot")
            @PathVariable
            @Positive(message = "El ID de Detalle debe ser positivo")
            Long idDetalle) {

        log.info("Solicitud de brindar slot: {} recibida", idDetalle);
        SlotClientResponse slot = horarioService.brindarSlot(idHorario, idDetalle);
        log.info("Solicitud de brindar slot: {} terminada, respuesta enviada", idDetalle);

        return ResponseEntity.ok(slot);
    }

}

