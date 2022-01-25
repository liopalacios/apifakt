package pe.com.certifakt.apifact.controller.webapp;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.certifakt.apifact.model.UnitCode;
import pe.com.certifakt.apifact.security.CurrentUser;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.UnitCodeService;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UnitCodeApi {

    private final UnitCodeService unitCodeService;

    @GetMapping("/search-unitcode")
    public ResponseEntity<?> searchUnidades(@CurrentUser UserPrincipal user,
                                            @RequestParam(name = "unidad", required = true) String unidad) {
        return new ResponseEntity<Object>(unitCodeService.searchUnitCode(unidad), HttpStatus.OK);
    }


    @GetMapping("company/myunitcodes")
    public ResponseEntity<?> unidades(@CurrentUser UserPrincipal user) {
        return new ResponseEntity<Object>(unitCodeService.getUnitCodesByCompany(user.getRuc()), HttpStatus.OK);
    }

    @PostMapping("company/myunitcodes")
    public ResponseEntity<?> agregar(@CurrentUser UserPrincipal user, @RequestBody UnitCode unitCode) {
        return new ResponseEntity<Object>(unitCodeService.addUnitCode(user.getRuc(), unitCode), HttpStatus.OK);
    }

    @PostMapping("company/myunitcodes/default")
    public ResponseEntity<?> setDefault(@CurrentUser UserPrincipal user, @RequestBody UnitCode unitCode) {
        return new ResponseEntity<Object>(unitCodeService.setDefaultUnitCode(user.getRuc(), unitCode), HttpStatus.OK);
    }

    @DeleteMapping("company/myunitcodes/{idUnitCode}")
    public ResponseEntity<?> quitar(@CurrentUser UserPrincipal user, @PathVariable Long idUnitCode) {
        return new ResponseEntity<Object>(unitCodeService.removeUnitCode(user.getRuc(), idUnitCode), HttpStatus.OK);
    }

    @DeleteMapping("company/myunitcodes/default/remove")
    public ResponseEntity<?> quitarDefault(@CurrentUser UserPrincipal user) {
        return new ResponseEntity<Object>(unitCodeService.removeDefaultUnitCode(user.getRuc()), HttpStatus.OK);
    }


}
