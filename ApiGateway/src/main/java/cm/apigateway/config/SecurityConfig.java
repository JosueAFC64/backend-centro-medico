package cm.apigateway.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@Getter
public class SecurityConfig {

    /**
     * Lista de rutas públicas que no requieren autenticación JWT.
     * Puedes agregar más rutas según tus necesidades.
     */
    @Value("${application.security.public-paths}")
    private String publicPathsConfig;

    /**
     * Obtiene la lista de rutas públicas parseadas
     *
     * @return Lista de rutas públicas
     */
    public List<String> getPublicPaths() {
        return Arrays.asList(publicPathsConfig.split(","));
    }

    /**
     * Verifica si una ruta es pública
     *
     * @param path La ruta a verificar
     * @return true si la ruta es pública, false en caso contrario
     */
    public boolean isPublicPath(String path) {
        List<String> publicPaths = getPublicPaths();
        return publicPaths.stream()
                .anyMatch(publicPath -> {
                    String publicPathTrimmed = publicPath.trim();

                    // Si el path público termina con /**
                    if (publicPathTrimmed.endsWith("/**")) {
                        String basePath = publicPathTrimmed.substring(0, publicPathTrimmed.length() - 3);
                        return path.startsWith(basePath);
                    }
                    // Si el path público termina con *
                    else if (publicPathTrimmed.endsWith("/*")) {
                        String basePath = publicPathTrimmed.substring(0, publicPathTrimmed.length() - 2);
                        return path.startsWith(basePath) &&
                                path.substring(basePath.length()).indexOf('/') == -1;
                    }
                    // Coincidencia exacta
                    else {
                        return path.equals(publicPathTrimmed);
                    }
                });
    }
}


