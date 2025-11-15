package cm.apiusuarios.controller;

import cm.apiusuarios.dto.UserCookieResponse;
import cm.apiusuarios.dto.UserRequest;
import cm.apiusuarios.dto.UserResponse;
import cm.apiusuarios.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Validated
@Tag(name = "Gestión de Usuarios", description = "API para gestionar usuarios del sistema de centro médico")
public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<Void> registrar(
            @Parameter(description = "Datos requeridos para registrar al usuario")
            @RequestBody
            @Valid
            UserRequest request) {

        log.info("Solicitud de registrar usuario: {} recibida", request);
        service.registrar(request);
        log.info("Solicitud de registrar usuario terminada, respuesta enviada");

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> listar() {

        log.info("Solicitud de listar usuarios recibida");
        List<UserResponse> users = service.listar();
        log.info("Solicitud de listar usuarios terminada, respuesta enviada");

        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> buscarPorId(
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long id) {

        log.info("Solicitud de buscar usuario por ID: {} recibida", id);
        UserResponse user = service.buscarPorId(id);
        log.info("Solicitud de buscar usuario por ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/session/user-data")
    public ResponseEntity<UserCookieResponse> getUserInSessionData(HttpServletRequest request) {

        log.info("Solicitud de obtener datos de usuario en sesión recibida");
        UserCookieResponse user = service.getUserInSessionData(request);
        log.info("Solicitud de obtener datos de usuario en sesión terminada, respuesta enviada");

        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable
            @Positive(message = "El ID debe ser positivo")
            Long id) {

        log.info("Solicitud de eliminar usuario con ID: {} recibida", id);
        service.eliminar(id);
        log.info("Solicitud de eliminar usuario con ID: {} terminada, respuesta enviada", id);

        return ResponseEntity.noContent().build();
    }

}
