package pe.com.certifakt.apifact.controller.webapp;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.com.certifakt.apifact.bean.RazonSocial;
import pe.com.certifakt.apifact.service.SunatService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class SunatApi {

    private SunatService sunatService;

    @GetMapping("/sunat-buscar-ruc")
    public ResponseEntity<RazonSocial> consultarRUC(@RequestParam(name = "ruc", required = true) String ruc,
                                                    HttpServletRequest request, HttpServletResponse response, Principal principal) {
        return new ResponseEntity<RazonSocial>(sunatService.findRazonSocialByRUC(ruc), HttpStatus.OK);
    }

    @GetMapping("/reniec-buscar-dni")
    public ResponseEntity<?> consultarDNI(@RequestParam(name = "dni", required = true) String dni,
                                          HttpServletRequest request, HttpServletResponse response, Principal principal) {
        return new ResponseEntity<>(ImmutableMap.of("nombre", sunatService.findNombreByDNI(dni)), HttpStatus.OK);
    }
}
