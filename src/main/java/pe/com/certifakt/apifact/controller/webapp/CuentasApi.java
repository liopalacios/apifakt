package pe.com.certifakt.apifact.controller.webapp;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.certifakt.apifact.model.CuentaEntity;
import pe.com.certifakt.apifact.repository.BancoRepository;
import pe.com.certifakt.apifact.security.CurrentUser;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.CompanyService;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class CuentasApi {

    private final CompanyService companyService;
    private final BancoRepository bancoRepository;


    @GetMapping("/bancos")
    public ResponseEntity<?> bancos() {
        return new ResponseEntity<Object>(bancoRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/cuentas")
    public ResponseEntity<?> listar(@CurrentUser UserPrincipal user) {
        return new ResponseEntity<Object>(companyService.cuentasByRuc(user.getRuc()), HttpStatus.OK);
    }

    @PostMapping("/cuentas")
    public ResponseEntity<?> guardar(@CurrentUser UserPrincipal user, @RequestBody CuentaEntity cuentaEntity) {
        return new ResponseEntity<Object>(companyService.addCuentaByRuc(user.getRuc(), cuentaEntity), HttpStatus.OK);
    }


    @PatchMapping("/cuentas")
    public ResponseEntity<?> remover(@CurrentUser UserPrincipal user, @RequestBody CuentaEntity cuentaEntity) {
        return new ResponseEntity<Object>(companyService.removeCuentaByRuc(user.getRuc(), cuentaEntity), HttpStatus.OK);
    }


}
