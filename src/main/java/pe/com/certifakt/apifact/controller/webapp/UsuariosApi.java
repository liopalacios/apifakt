package pe.com.certifakt.apifact.controller.webapp;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.certifakt.apifact.bean.ChangePassword;
import pe.com.certifakt.apifact.model.User;
import pe.com.certifakt.apifact.security.CurrentUser;
import pe.com.certifakt.apifact.security.JwtTokenProvider;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.UserService;

import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api")
public class UsuariosApi {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @GetMapping("/usuarios")
    public ResponseEntity<?> usuarios(@CurrentUser UserPrincipal user) {
        return new ResponseEntity<Object>(userService.findAllByCompanyId(user.getRuc()), HttpStatus.OK);
    }

    @GetMapping("/usuarios/generar-token-api/{idUser}")
    public ResponseEntity<?> generarTokenApk(@PathVariable Long idUser) {
        return new ResponseEntity<Object>(tokenProvider.generateTokenApi(idUser), HttpStatus.OK);
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<?> usuario(@CurrentUser UserPrincipal user, @PathVariable Long id) {
        return new ResponseEntity<Object>(userService.findById(id), HttpStatus.OK);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<?> guardar(@RequestBody User user, @CurrentUser UserPrincipal usera) {
        return new ResponseEntity<Object>(userService.saveUser(user, false, usera), HttpStatus.OK);
    }

    @PutMapping("/usuarios")
    public ResponseEntity<?> editar(@RequestBody User user, @CurrentUser UserPrincipal usera) {
        return new ResponseEntity<Object>(userService.saveUser(user, true, usera), HttpStatus.OK);
    }

    @PutMapping("/usuarios/oficinas")
    public void asignar(@CurrentUser UserPrincipal user, @RequestBody Map<String, Object> body) {
        Long idUsuario = Long.valueOf(body.get("idUsuario").toString());
        Integer idOficina = null;
        if (body.get("idOficina") != null) {
            idOficina = (Integer) body.get("idOficina");
        }
        userService.asignarOficinaUser(idUsuario, idOficina);
    }

    @DeleteMapping("/usuarios/{id}")
    public void borrar(@PathVariable Long id, @CurrentUser UserPrincipal user) {
        userService.deleteUser(id);
    }

    @PatchMapping("/usuarios")
    public ResponseEntity<?> modContra(@RequestBody ChangePassword change, @CurrentUser UserPrincipal user) {
        return new ResponseEntity<Boolean>(userService.changePass(user.getId(), change), HttpStatus.OK);
    }


}
