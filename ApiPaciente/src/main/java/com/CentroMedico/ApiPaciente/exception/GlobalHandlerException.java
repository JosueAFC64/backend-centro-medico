package com.CentroMedico.ApiPaciente.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalHandlerException {
    /**
     * Maneja las excepciones de validación de argumentos (@Valid en el Controller).
     * Devuelve HttpStatus 400 BAD_REQUEST.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        body.put("errors", errors);

        log.error("Error de validación: {}", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja las excepciones de argumento ilegal (ej. DNI ya existe, fecha inválida, ID/DNI no encontrado).
     * Devuelve HttpStatus 400 BAD_REQUEST o 404 NOT_FOUND.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (ex.getMessage().contains("no encontrado")) {
            status = HttpStatus.NOT_FOUND;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", "Error de Operación");
        body.put("message", ex.getMessage());

        log.warn("Excepción de lógica de negocio ({}): {}", status, ex.getMessage());
        return new ResponseEntity<>(body, status);
    }

    /**
     * Maneja cualquier otra excepción no prevista.
     * Devuelve HttpStatus 500 INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Error Interno del Servidor");
        body.put("message", "Ocurrió un error inesperado. Consulte los logs.");

        log.error("Error no controlado: ", ex);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Maneja las excepciones de estado ilegal (ej. No se puede eliminar por restricción de negocio).
     * Devuelve HttpStatus 409 CONFLICT.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(IllegalStateException ex) {
        HttpStatus status = HttpStatus.CONFLICT; // 409 Conflict o 400 Bad Request

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", "Restricción de Integridad");
        body.put("message", ex.getMessage());

        log.warn("Excepción de estado ilegal ({}): {}", status, ex.getMessage());
        return new ResponseEntity<>(body, status);
    }
}
