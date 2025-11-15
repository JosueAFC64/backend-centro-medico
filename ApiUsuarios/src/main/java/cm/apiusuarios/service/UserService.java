package cm.apiusuarios.service;

import cm.apiusuarios.dto.UserCookieResponse;
import cm.apiusuarios.dto.UserRequest;
import cm.apiusuarios.dto.UserResponse;
import cm.apiusuarios.repository.user.User;
import cm.apiusuarios.repository.user.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public void registrar(UserRequest request) {
        User user = User.builder()
                .nombreUsuario(request.nombreUsuario())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .rol(request.rol())
                .build();

        repository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listar() {
        List<User> users = repository.findAll();

        return users.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse buscarPorId(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con ID: " + id + " no encontrado"));

        return toResponse(user);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Usuario con ID: " + id + " no encontrado");
        }

        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public UserCookieResponse getUserInSessionData(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("USER_SESSION")) {
                    Claims claims = jwtService.extractPayload(cookie.getValue());
                    String email = claims.getSubject();

                    User user = repository.findByEmail(email)
                            .orElseThrow(() ->
                                    new EntityNotFoundException("Usuario con email: " + email + " no encontrado"));

                    return toCookieResponse(user);
                }
            }
        }
        return null;
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getNombreUsuario(),
                user.getEmail(),
                user.getEstado(),
                user.getRol()
        );
    }

    private UserCookieResponse toCookieResponse(User user) {
        return new UserCookieResponse(
                user.getId(),
                user.getNombreUsuario(),
                user.getEmail(),
                user.getRol()
        );
    }

}
