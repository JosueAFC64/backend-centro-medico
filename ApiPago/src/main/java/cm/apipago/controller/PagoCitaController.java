package cm.apipago.controller;

import cm.apipago.dto.pagocita.PagoCitaRequest;
import cm.apipago.dto.pagocita.PagoCitaResponse;
import cm.apipago.exceptions.ErrorResponse;
import cm.apipago.service.PagoCitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/pago-cita")
@RequiredArgsConstructor
@Validated
@Tag(name = "Pago de Citas", description = "API para gestión de pago de citas")
public class PagoCitaController {

    private final PagoCitaService service;

    @PostMapping
    @Operation(summary = "Registrar nuevo pago cita",
            description = "Crea un nuevo pago cita cuando una cita médica es marcada como COMPLETADA")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pago Cita creada exitosamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> registrar(
            @Parameter(description = "Datos requeridos para registrar el pago de cita")
            @RequestBody
            @Valid
            PagoCitaRequest request) {

        service.registrar(request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{dniPaciente}")
    @Operation(summary = "Buscar pago cita por DNI de paciente",
            description = "Obtiene un pago cita específico por DNI del paciente")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pago Cita obtenido correctamente",
                    content = @Content(schema = @Schema(implementation = PagoCitaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pago Cita no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<PagoCitaResponse>> buscarPorDniPaciente(
            @Parameter(description = "DNI único del paciente")
            @Size(min = 1, max = 8, message = "El DNI debe tener 8 dígitos")
            @PathVariable String dniPaciente) {

        log.info("Solicitud de buscar por DNI: {} recibida", dniPaciente);
        List<PagoCitaResponse> pagoCitas = service.buscarPorDniPaciente(dniPaciente);
        log.info("Solicitud de buscar por DNI: {} terminada, respuesta enviada", dniPaciente);

        return ResponseEntity.ok().body(pagoCitas);
    }

}
