package cm.apigateway.filter;

import cm.apigateway.config.SecurityConfig;
import cm.apigateway.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String COOKIE_NAME = "USER_SESSION";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    private final JwtService jwtService;
    private final SecurityConfig securityConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Si la ruta es pública, permitir el acceso sin autenticación
        if (securityConfig.isPublicPath(path)) {
            log.debug("Ruta pública detectada: {}", path);
            return chain.filter(exchange);
        }

        // Extraer el token de la cookie USER_SESSION
        String token = extractToken(request);

        // Si no hay token, rechazar la solicitud
        if (!StringUtils.hasText(token)) {
            log.warn("Token no encontrado para la ruta: {}", path);
            return onError(exchange, "Token no encontrado");
        }

        // Validar el token
        if (!jwtService.validateToken(token)) {
            log.warn("Token inválido para la ruta: {}", path);
            return onError(exchange, "Token inválido o expirado");
        }

        // Extraer información del token y agregarla a los headers para los servicios downstream
        try {
            String email = jwtService.extractUsername(token);
            String role = jwtService.extractRole(token);

            // Agregar información del usuario a los headers de la solicitud
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header(USER_EMAIL_HEADER, email)
                    .header(USER_ROLE_HEADER, role != null ? role : "")
                    .build();

            log.debug("Token válido para usuario: {} con rol: {}", email, role);

            // Continuar con la cadena de filtros con la solicitud modificada
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception e) {
            log.error("Error al procesar el token JWT: {}", e.getMessage());
            return onError(exchange, "Error al procesar el token");
        }
    }

    /**
     * Extrae el token JWT de la cookie USER_SESSION
     *
     * @param request La solicitud HTTP
     * @return El token JWT o null si no se encuentra
     */
    private String extractToken(ServerHttpRequest request) {
        var cookie = request.getCookies().getFirst(COOKIE_NAME);
        return cookie != null ? cookie.getValue() : null;
    }

    /**
     * Maneja errores de autenticación
     *
     * @param exchange El intercambio de servidor web
     * @param message  Mensaje de error
     * @return Mono vacío
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");

        String body = String.format("{\"error\": \"%s\", \"status\": %d}", message, HttpStatus.UNAUTHORIZED.value());
        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes()))
        );
    }

    @Override
    public int getOrder() {
        // Orden alto para que se ejecute temprano en la cadena de filtros
        return -100;
    }
}

