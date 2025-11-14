package cm.apigateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    /**
     * Valida el token JWT y verifica que no esté expirado
     *
     * @param token El token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrae el subject (email) del token
     *
     * @param token JWT del que se tiene que extraer el subject
     * @return El texto plano del subject (email)
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae el rol del usuario del token
     *
     * @param token JWT del que se tiene que extraer el rol
     * @return El rol del usuario como String
     */
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("rol", String.class);
    }

    /**
     * Extrae un claim específico del token
     *
     * @param token JWT del que se extraerá el claim
     * @param claimsResolver Función para resolver el claim
     * @param <T> Tipo del claim
     * @return El valor del claim
     */
    private <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token
     *
     * @param token JWT del que se extraerán los claims
     * @return Objeto Claims con todos los claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Genera la clave secreta (SecretKey) usada para verificar tokens JWT.
     *
     * @return Clave secreta en formato HMAC-SHA.
     */
    private SecretKey getSigningKey() {
        final byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
