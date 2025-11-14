package cm.apiusuarios.service;

import cm.apiusuarios.repository.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Genera un nuevo JWT
     *
     * @param user Usuario al que se le generará el token
     * @return El resultado del servicio buildToken
     * */
    public String generateToken(final User user) {return buildToken(user, jwtExpiration);}

    /**
     * Construye el JWT (rol, email, iat, exp)
     *
     * @param user Usuario al que se le construirá el token
     * @param expiration Tiempo de duración del token
     * @return JWT construido
     * */
    public String buildToken(final User user, final long expiration){
        return Jwts.builder()
                .claims(Map.of(
                        "rol", user.getRol()
                ))
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Genera la clave secreta (SecretKey) usada para firmar/verificar tokens JWT.
     *
     * @return Clave secreta en formato HMAC-SHA.
     */
    private SecretKey getSigningKey() {
        final byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
