package pe.com.certifakt.apifact.controller.webapp;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.MultipartFile;
import pe.com.certifakt.apifact.model.ClientEntity;
import pe.com.certifakt.apifact.security.CurrentUser;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.ClientService;
import pe.com.certifakt.apifact.service.SunatService;
import pe.com.certifakt.apifact.util.CsvUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ClientesApi {

    private final ClientService clientService;
    private SunatService sunatService;


    @GetMapping("/clientes")
    public ResponseEntity<?> clientes(@CurrentUser UserPrincipal user) {
        return new ResponseEntity<Object>(clientService.findAllByCompanyId(user.getRuc()), HttpStatus.OK);
    }

    @GetMapping("/clientespage")
    public Map<String, Object> productospage(@CurrentUser UserPrincipal user,
                                             @RequestParam(value = "pagenumber", required = false) int pagenumber,
                                             @RequestParam(value = "filter", required = false) String filter,
                                             @RequestParam(value = "perpage", required = false) int perpage) {
        return clientService.findByCompanyId(pagenumber, perpage, user, filter);

    }

    @GetMapping("/clientebyTypeNum")
    public ResponseEntity<?> cliente(@CurrentUser UserPrincipal user,
                                     @RequestParam(name = "tipo", required = true) String tipo,
                                     @RequestParam(name = "numero", required = true) String numero) {
        return new ResponseEntity<Object>(clientService.findByCompanyIdAndDocumento(user.getRuc(), tipo, numero), HttpStatus.OK);
    }

    @PostMapping("/clientes")
    public ResponseEntity<?> guardar(@RequestBody ClientEntity clientEntity, @CurrentUser UserPrincipal user) throws Exception {
        return new ResponseEntity<Object>(clientService.saveClient(clientEntity, false, user), HttpStatus.OK);
    }

    @PutMapping("/clientes")
    public ResponseEntity<?> editar(@RequestBody ClientEntity clientEntity, @CurrentUser UserPrincipal user) throws Exception {
        return new ResponseEntity<Object>(clientService.saveClient(clientEntity, true, user), HttpStatus.OK);
    }

    @Bean
    CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }

    @RequestMapping(value = "/arrayclientes", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    @Transactional
    public Map<String, Object> guardararray(@RequestParam("filecsv") MultipartFile listObj, @CurrentUser UserPrincipal user) throws IOException {
        characterEncodingFilter();

        List<ClientEntity> clients = CsvUtils.read(ClientEntity.class, listObj.getInputStream());
        Map<String, Object> mapValidate = clientService.valiteAll(clients);
        if ((Integer) (mapValidate.get("status")) != 200) {

            return mapValidate;

        } else {

            clientService.saveAll(clients, user);
            return ImmutableMap.of("listProductosObs", clients, "Obs ", "Registro satisfactorio", "status", (Integer) (mapValidate.get("status")));

        }

    }

    @DeleteMapping("/clientes/{id}")
    public void borrar(@PathVariable Long id, @CurrentUser UserPrincipal user) {
        clientService.deleteClient(id, user);
    }

}
