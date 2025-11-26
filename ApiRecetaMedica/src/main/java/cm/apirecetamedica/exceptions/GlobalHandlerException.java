package cm.apirecetamedica.exceptions;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String uri = obtenerUri(request);

        log.warn("IllegalArgumentException manejada: {} - URI: {}", ex.getMessage(), uri, ex);

        ErrorResponse errorResponse = toErrorResponse(
                status,
                ex.getMessage(),
                uri
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        String uri = obtenerUri(request);

        log.warn("EntityNotFoundException manejada: {} - URI: {}", ex.getMessage(), uri, ex);

        ErrorResponse errorResponse = toErrorResponse(
                status,
                ex.getMessage(),
                uri
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(
            ServiceUnavailableException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        String uri = obtenerUri(request);

        log.warn("ServiceUnavailableException manejada: {} - URI: {}", ex.getMessage(), uri, ex);

        ErrorResponse errorResponse = toErrorResponse(
                status,
                ex.getMessage(),
                uri
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            WebRequest request){

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String uri = obtenerUri(request);

        log.warn("MethodArgumentNotValidException manejada: {} - URI: {}", ex.getMessage(), uri, ex);

        ErrorResponse errorResponse = toErrorResponse(
                status,
                ex.getMessage(),
                uri
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErrorResponse> handleMissingPathVariableException(
            MissingPathVariableException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String uri = obtenerUri(request);

        log.warn("MissingPathVariableException manejada: {} - URI: {}", ex.getMessage(), uri, ex);

        ErrorResponse errorResponse = toErrorResponse(
                status,
                ex.getMessage(),
                uri
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String uri = obtenerUri(request);

        log.warn("HttpMessageNotReadableException manejada {} - URI: {}", ex.getMessage(), uri, ex);

        ErrorResponse errorResponse = toErrorResponse(
                status,
                ex.getMessage(),
                uri
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String uri = obtenerUri(request);

        log.warn("ConstraintViolationException manejada {} - URI: {}", ex.getMessage(), uri, ex);

        ErrorResponse errorResponse = toErrorResponse(
                status,
                ex.getMessage(),
                uri
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String uri = obtenerUri(request);

        log.error("Exception no manejada: {} - URI: {}", ex.getMessage(), uri, ex);

        ErrorResponse errorResponse = toErrorResponse(
                status,
                ex.getMessage(),
                uri
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    private String obtenerUri(WebRequest request){
        String uri = request.getDescription(false);
        return uri.startsWith("uri=") ?
                uri.substring(4) : uri;
    }

    private ErrorResponse toErrorResponse(HttpStatus status, String message, String path){
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
    }

}
