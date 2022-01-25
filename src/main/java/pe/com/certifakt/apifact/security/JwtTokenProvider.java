package pe.com.certifakt.apifact.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.dto.TokenDTO;
import pe.com.certifakt.apifact.util.UtilDate;

import java.util.Date;


@Slf4j
@Component
public class JwtTokenProvider {


    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    public TokenDTO generateToken(Authentication authentication) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        String token = Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        return TokenDTO.builder()
                .token(token)
                .expira(expiryDate)
                .build();
    }

    public TokenDTO generateTokenApi(Long idUser) {

        Date now = new Date();
        Date expiryDate = UtilDate.sumarDiasAFecha(now, 1000);

        String token = Jwts.builder()
                .setSubject(Long.toString(idUser))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        return TokenDTO.builder()
                .token(token)
                .expira(expiryDate)
                .build();
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Firma del Token invalida");
        } catch (MalformedJwtException ex) {
            log.error("Token Invalido");
        } catch (ExpiredJwtException ex) {
            log.error("El Token ha expirado");
        } catch (UnsupportedJwtException ex) {
            log.error("No soporta JWT");
        } catch (IllegalArgumentException ex) {
            log.error("Token vacio");
        }
        return false;
    }
}
