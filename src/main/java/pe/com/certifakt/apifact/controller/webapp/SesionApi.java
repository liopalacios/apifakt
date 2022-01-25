package pe.com.certifakt.apifact.controller.webapp;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.com.certifakt.apifact.model.BranchOfficeEntity;
import pe.com.certifakt.apifact.model.CompanyEntity;
import pe.com.certifakt.apifact.security.CurrentUser;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class SesionApi {

    private final CatalogoService catalogoService;
    private final ParameterService parameterService;
    private final UserService userService;
    private final UnitCodeService unitCodeService;
    private final UbigeoService ubigeoService;
    private final UbigeototalService ubigeototalService;
    private final CompanyService companyService;

    @GetMapping("/dataInicial")
    public ResponseEntity<?> dataInicial(@CurrentUser UserPrincipal user) {
        Map<String, Object> data = new HashMap<>();
        data = catalogoService.getCatalogInicial();
        BranchOfficeEntity ofi = userService.getOfficeFromUser(user.getId());
        if (ofi != null) {
            data.put("idPuntoVenta", ofi.getId());
        }
        CompanyEntity companyEntity = companyService.getCompanyByRuc(user.getRuc());
        data.put("company", companyEntity);
        data.put("catalogo06", catalogoService.getListCatalog06());
        data.put("catalogo07", catalogoService.getListCatalog07());
        data.put("catalogo09", catalogoService.getListCatalog09());
        data.put("catalogo10", catalogoService.getListCatalog10());
        data.put("catalogo17", catalogoService.getListCatalog17());
        data.put("catalogo18", catalogoService.getListCatalog18());
        data.put("catalogo20", catalogoService.getListCatalog20());
        data.put("catalogo59", catalogoService.getListCatalog59());
        data.put("catalogo54", catalogoService.getListCatalog54());
        data.put("ubigeo", ubigeoService.findAllDepartamento());
        data.put("parametros", parameterService.getParametersList());
        data.put("cuentas", companyService.cuentasByRuc(user.getRuc()));
        data.put("misunidades", unitCodeService.getUnitCodesByCompany(user.getRuc()));
        data.put("ubigeoTotal", ubigeototalService.findAllUbigeo());
        return new ResponseEntity<Object>(data, HttpStatus.OK);
    }


}
