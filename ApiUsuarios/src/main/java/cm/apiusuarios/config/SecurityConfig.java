package cm.apiusuarios.config;

import cm.apiusuarios.repository.token.Token;
import cm.apiusuarios.repository.token.TokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenRepository tokenRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .authorizeHttpRequests(req ->
                        req
                                .anyRequest().permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/auth/logout")
                                .addLogoutHandler(this::logout)
                                .deleteCookies("USER_SESSION")
                                .invalidateHttpSession(true)
                                .logoutSuccessHandler(
                                        (req,
                                         resp,
                                         auth) ->
                                                resp.setStatus(HttpStatus.OK.value())
                                )
                )

        ;

        return http.build();
    }

    private void logout(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("USER_SESSION")) {
                    String jwt = cookie.getValue();

                    final Token storedToken = tokenRepository.findByToken(jwt);
                    if (storedToken != null) {
                        storedToken.setIsExpired(true);
                        storedToken.setIsRevoked(true);
                        tokenRepository.save(storedToken);
                    }

                    break;
                }
            }
        }
        SecurityContextHolder.clearContext();
    }


}
