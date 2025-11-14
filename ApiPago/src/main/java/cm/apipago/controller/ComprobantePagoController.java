package cm.apipago.controller;

import cm.apipago.dto.comprobantepago.ComprobantePagoRequest;
import cm.apipago.dto.comprobantepago.ComprobantePagoResponse;
import cm.apipago.exceptions.ErrorResponse;
import cm.apipago.service.ComprobantePagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/comprobantes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Comprobantes de Pago", description = "API para gestión de comprobantes de pago")
public class ComprobantePagoController {

    private final ComprobantePagoService service;

    @PostMapping(produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Registrar nuevo comprobante de pago",
            description = "Crea un nuevo comprobante de pago y marca los pago de citas asociados como PAGADO")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Comprobante de pago creado exitosamente",
                    content = @Content(schema = @Schema(implementation = ComprobantePagoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<byte[]> registrar(
            @Parameter(description = "Datos requeridos para registrar el comprobante de pago")
            @RequestBody
            @Valid
            ComprobantePagoRequest request) {

        byte[] created = service.registrar(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comprobante-pago.pdf")
                .body(created);
    }

}
