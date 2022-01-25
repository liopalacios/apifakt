package pe.com.certifakt.apifact.controller.webapp;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.certifakt.apifact.model.BranchOfficeEntity;
import pe.com.certifakt.apifact.model.SerieEntity;
import pe.com.certifakt.apifact.security.CurrentUser;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.BranchOfficeService;
import pe.com.certifakt.apifact.service.SerieService;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class OficinaApi {

    private final BranchOfficeService branchOfficeService;

    private final SerieService serieService;


    @GetMapping("/oficinas")
    public ResponseEntity<?> oficinas(@CurrentUser UserPrincipal user) {
        return new ResponseEntity<Object>(branchOfficeService.findAllByCompanyId(user.getRuc()), HttpStatus.OK);
    }

    @GetMapping("/oficinas/{id}")
    public ResponseEntity<?> oficinas(@CurrentUser UserPrincipal user, @PathVariable Integer id) {
        return new ResponseEntity<Object>(branchOfficeService.findById(id), HttpStatus.OK);
    }

    @PostMapping("/oficinas")
    public ResponseEntity<?> guardar(@RequestBody BranchOfficeEntity branchOfficeEntity, @CurrentUser UserPrincipal user) {
        return new ResponseEntity<Object>(branchOfficeService.saveBranchOffice(branchOfficeEntity, false, user), HttpStatus.OK);
    }

    @PutMapping("/oficinas")
    public ResponseEntity<?> editar(@RequestBody BranchOfficeEntity branchOfficeEntity, @CurrentUser UserPrincipal user) {
        return new ResponseEntity<Object>(branchOfficeService.saveBranchOffice(branchOfficeEntity, true, user), HttpStatus.OK);
    }

    @DeleteMapping("/oficinas/{id}")
    public void borrar(@PathVariable Integer id, @CurrentUser UserPrincipal user) {
        branchOfficeService.deleteBranchOffice(id, user.getUsername());
    }

    //SERIES
    @GetMapping("/oficinas/{idOficina}/series")
    public ResponseEntity<?> series(@CurrentUser UserPrincipal user, @PathVariable Integer idOficina) {
        return new ResponseEntity<Object>(serieService.findAllByBranchOfficeId(idOficina), HttpStatus.OK);
    }

    @PostMapping("/oficinas/{idOficina}/series")
    public ResponseEntity<?> seriesSave(@CurrentUser UserPrincipal user, @PathVariable Integer idOficina, @RequestBody SerieEntity serie) {
        return new ResponseEntity<Object>(serieService.saveSerie(serie, idOficina, false, user.getUsername()), HttpStatus.OK);
    }

    @PutMapping("/oficinas/{idOficina}/series")
    public ResponseEntity<?> seriesEditar(@CurrentUser UserPrincipal user, @PathVariable Integer idOficina, @RequestBody SerieEntity serie) {
        return new ResponseEntity<Object>(serieService.saveSerie(serie, idOficina, true, user.getUsername()), HttpStatus.OK);
    }

    @DeleteMapping("/oficinas/{idOficina}/series/{idSerie}")
    public void seriesBorrar(@CurrentUser UserPrincipal user, @PathVariable Integer idOficina, @PathVariable Integer idSerie) {
        serieService.deleteSerie(idSerie);
    }


}
