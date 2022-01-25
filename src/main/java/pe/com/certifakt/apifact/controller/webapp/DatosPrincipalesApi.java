package pe.com.certifakt.apifact.controller.webapp;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.certifakt.apifact.model.CompanyEntity;
import pe.com.certifakt.apifact.security.CurrentUser;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.CompanyService;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class DatosPrincipalesApi {

    private final CompanyService companyService;


    @GetMapping("/miempresa")
    public ResponseEntity<?> miempresa(@CurrentUser UserPrincipal user) {

        return new ResponseEntity<Object>(companyService.getCompanyByRuc(user.getRuc()), HttpStatus.OK);
    }

    @PostMapping("/miempresa")
    public ResponseEntity<?> miempresa(@CurrentUser UserPrincipal user, @RequestBody CompanyEntity empresa) {

        return new ResponseEntity<Object>(companyService.actualizarDatosEmpresa(user.getRuc(), empresa), HttpStatus.OK);
    }


}
