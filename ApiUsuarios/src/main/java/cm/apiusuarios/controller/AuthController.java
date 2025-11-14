package cm.apiusuarios.controller;

import cm.apiusuarios.dto.AuthRequest;
import cm.apiusuarios.service.AuthService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Autenticación", description = "API para autenticar usuarios")
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public ResponseEntity<Void> autenticar(
            @Parameter(description = "Datos requeridos para autenticarse")
            @RequestBody
            @Valid
            AuthRequest request,

            HttpServletResponse response) {

        log.info("Solicitud de autenticarse recibida");
        service.authenticate(request, response);
        log.info("Solicitud de autenticarse terminada, respuesta enviada");

        return ResponseEntity.noContent().build();
    }

}
