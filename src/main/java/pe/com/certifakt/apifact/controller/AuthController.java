package pe.com.certifakt.apifact.controller;

import lombok.extern.slf4j.Slf4j;
import pe.com.certifakt.apifact.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pe.com.certifakt.apifact.bean.*;
import pe.com.certifakt.apifact.dto.TokenDTO;
import pe.com.certifakt.apifact.model.User;
import pe.com.certifakt.apifact.security.CurrentUser;
import pe.com.certifakt.apifact.security.JwtTokenProvider;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.UserService;
import pe.com.certifakt.apifact.util.ConstantesParameter;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    UserService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDTO tokenDTO = tokenProvider.generateToken(authentication);


        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = usuarioService.findById(principal.getId());
        List<String> roles;
        CompanySummary empresa = new CompanySummary();
        if (user != null) {


            if (user.getCompany().getEstado() == null || !user.getCompany().getEstado().equals(ConstantesParameter.REGISTRO_ACTIVO))
                throw new ServiceException("Tu empresa se encuentra dada de baja en Certifakt.");

            if (user.getEstado() == null || !user.getEstado())
                throw new ServiceException("Usuario inactivo");

            roles = user.getAuthorities().stream().map(r -> r.getName().toString()).collect(Collectors.toList());
            empresa.setRuc(user.getCompany().getRuc());
            empresa.setId(user.getCompany().getId());
            empresa.setRazonSocial(user.getCompany().getRazonSocial());
            empresa.setNombreComercial(user.getCompany().getNombreComercial());
            empresa.setViewGuia(user.getCompany().getViewGuia());
            empresa.setViewOtroComprobante(user.getCompany().getViewOtroComprobante());

            JwtAuthenticationResponse resp = JwtAuthenticationResponse.builder()
                    .accessToken(tokenDTO.getToken())
                    .expires(tokenDTO.getExpira())
                    .user(UserSummary.builder()
                            .id(principal.getId())
                            .fullName(principal.getName())
                            .razonSocial(empresa.getRazonSocial())
                            .nombreComercial(empresa.getNombreComercial())
                            .ruc(empresa.getRuc())
                            .typeUser(user.getTypeUser() != null ? user.getTypeUser() : "01")
                            .ublVersion("2.1")
                            .userName(principal.getUsername())
                            .build())
                    .tokenType("Bearer")
                    .roles(roles)
                    .build();

            return ResponseEntity.ok(resp);

        }
        throw new ServiceException("Usuario no encontrado");
    }

    @PostMapping("/register")
    public ResponseEntity<?> files(@RequestBody User user) throws ServiceException {
        return new ResponseEntity<Object>(usuarioService.registerUser(user), HttpStatus.OK);
    }



    @GetMapping("/me")
    public ResponseEntity<?> findUser(@CurrentUser UserPrincipal user) {
        return ResponseEntity.ok().body(usuarioService.findById(user.getId()));
    }


    @PatchMapping("/me/password")
    public ResponseEntity<?> modContra(@RequestBody ChangePassword change, @CurrentUser UserPrincipal user) {
        return new ResponseEntity<Boolean>(usuarioService.changePass(user.getId(), change), HttpStatus.OK);
    }

}
