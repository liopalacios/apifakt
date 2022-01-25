package pe.com.certifakt.apifact.controller.webapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.com.certifakt.apifact.model.ProductEntity;
import pe.com.certifakt.apifact.security.CurrentUser;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.CatalogoService;
import pe.com.certifakt.apifact.service.ProductService;
import pe.com.certifakt.apifact.util.CsvUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ProductosApi {

    private final ProductService productService;
    private final CatalogoService catalogoService;


    @GetMapping("/productos")
    public ResponseEntity<?> productos(@CurrentUser UserPrincipal user) {
        return new ResponseEntity<Object>(productService.findAllByCompanyId(user.getRuc()), HttpStatus.OK);
    }

    @GetMapping("/productospage")
    public Map<String, Object> productospage(@CurrentUser UserPrincipal user,
                                             @RequestParam(value = "pagenumber", required = false) int pagenumber,
                                             @RequestParam(value = "filter", required = false) String filter,
                                             @RequestParam(value = "perpage", required = false) int perpage) {
        return productService.findByCompanyId(pagenumber, perpage, user, filter);
    }


    @GetMapping("/search-productos")
    public ResponseEntity<?> searchProductos(@CurrentUser UserPrincipal user,
                                             @RequestParam(name = "producto", required = true) String producto) {
        return new ResponseEntity<Object>(productService.findByCompanyIdAndProd(user.getRuc(), producto), HttpStatus.OK);
    }

    @GetMapping("/search-codigo-sunat")
    public ResponseEntity<?> searchCodigoSunat(@RequestParam(name = "descripcion", required = true) String descripcion) {
        return new ResponseEntity<Object>(catalogoService.getListCatalogo25ByDescripcion(descripcion), HttpStatus.OK);
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<?> producto(@CurrentUser UserPrincipal user, @PathVariable Long id) {
        return new ResponseEntity<Object>(productService.findById(id), HttpStatus.OK);
    }

    @PostMapping("/productos")
    public ResponseEntity<?> guardar(@RequestBody ProductEntity productEntity, @CurrentUser UserPrincipal user) {
        return new ResponseEntity<Object>(productService.saveProduct(productEntity, false, user), HttpStatus.OK);
    }

    @PutMapping("/productos")
    public ResponseEntity<?> editar(@RequestBody ProductEntity productEntity, @CurrentUser UserPrincipal user) {
        return new ResponseEntity<Object>(productService.saveProduct(productEntity, true, user), HttpStatus.OK);
    }

    @RequestMapping(value = "/arrayproductos", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    @Transactional
    public Map<String, Object> guardararray(@RequestParam("filecsv") MultipartFile listObj, @CurrentUser UserPrincipal user) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<ProductEntity> products = CsvUtils.processInputFile(listObj.getInputStream());
        Map<String, Object> mapValidate = productService.valiteAll(products);
        if ((Integer) (mapValidate.get("status")) != 200) {
            return mapValidate;
        } else {
            updateAllUpperCase(products);
            ArrayList<String> codes = productService.saveAll(products, user);

            return ImmutableMap.of("listProductosObs", products, "Obs", codes, "status", (Integer) (mapValidate.get("status")));

        }

    }

    private void updateAllUpperCase(List<ProductEntity> products) {
        for (ProductEntity productEntity : products) {
            productEntity.setUnidadMedida(productEntity.getUnidadMedida().toUpperCase());
            productEntity.setMoneda(productEntity.getMoneda().toUpperCase());
            productEntity.setDescripcion(productEntity.getDescripcion().toUpperCase());

        }
    }




    @DeleteMapping("/productos/{id}")
    public void borrar(@PathVariable Long id, @CurrentUser UserPrincipal user) {
        productService.deleteProduct(id, user.getUsername());
    }


}
