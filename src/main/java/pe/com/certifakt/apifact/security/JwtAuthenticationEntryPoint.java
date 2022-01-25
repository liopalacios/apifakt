package pe.com.certifakt.apifact.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {

        if (e instanceof BadCredentialsException) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Usuario o contraseña incorrectos");
        } else if (e instanceof InsufficientAuthenticationException){
            /*httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Su sesión ha expirado");*/
        } else {
            logger.error("Responding with unauthorized error. Message - {}", e.getClass().getSimpleName());
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Error de autenticación");
        }
    }
}
