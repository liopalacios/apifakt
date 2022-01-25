package pe.com.certifakt.apifact.service.impl;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pe.com.certifakt.apifact.bean.UserResponse;
import pe.com.certifakt.apifact.model.CompanyEntity;
import pe.com.certifakt.apifact.model.ProductEntity;
import pe.com.certifakt.apifact.model.User;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.repository.ProductRepository;
import pe.com.certifakt.apifact.repository.UserRepository;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Override
    public List<ProductEntity> findAllByCompanyId(String ruc) {
        CompanyEntity companyEntity = companyRepository.findByRuc(ruc);
        return productRepository.findAllByCompanyIdAndEstadoIsTrueOrderByCreatedOnDesc(companyEntity.getId());
    }

    @Override
    public List<ProductEntity> findByCompanyIdAndProd(String ruc, String producto) {
        CompanyEntity companyEntity = companyRepository.findByRuc(ruc);
        return productRepository.searchProductos(companyEntity.getId(), producto, producto);
    }

    @Override
    public Map<String, Object> valiteAll(List<ProductEntity> products) {
        Integer estado = 200;
        List<ProductEntity> produstosObs = new ArrayList<>();
        List<String> strMnsags = new ArrayList<>();

        for (ProductEntity productEntity : products) {

            if (((Boolean) (validateEntity(productEntity)).get("status")).booleanValue()) {
                produstosObs.add(productEntity);
                strMnsags.add((String.valueOf((validateEntity(productEntity)).get("mensag"))).toUpperCase());
            }
        }

        if (produstosObs.size() > 0) {
            estado = 500;
        }
        return ImmutableMap.of("listProductosObs", produstosObs, "Obs ", strMnsags, "status", estado);
    }

    @Override
    public ArrayList<String> saveAll(List<ProductEntity> products, UserPrincipal user) {


        Optional<User> usuario = userRepository.findById(user.getId());
        ArrayList<String> codes = new ArrayList<String>();
        for (ProductEntity product : new ArrayList<>(products)) {
            product.setCreatedBy(user.getUsername());
            product.setCompany(usuario.get().getCompany());
            if (product.getCodigo().length() > 0) {
                ProductEntity productEntity = productRepository.findByCompanyIdAndCodigo(product.getCompany().getId(), product.getCodigo());

                if (productEntity != null) {
                    products.remove(product);
                    codes.add(productEntity.getCodigo());
                    if (!productEntity.getEstado()) {
                        productEntity.setEstado(true);
                        productRepository.save(productEntity);
                    }
                }
            }


        }
        productRepository.saveAll(products);
        return codes;
    }


    @Override
    public Map<String, Object> findByCompanyId(int pagenumber, int perpage, UserPrincipal user, String filter) {
        Optional<User> usuario = userRepository.findById(user.getId());
        CompanyEntity companyEntity = usuario.get().getCompany();

        if (filter.length() > 0) {
            long count = productRepository.countSearchByCompanyIdAndEstadoIsTrue(companyEntity.getId(), filter);
            Page<ProductEntity> listP = productRepository.searchProductosOrderByIdDesc(companyEntity.getId(), filter, new PageRequest(pagenumber - 1, perpage));
            return ImmutableMap.of("results", listP, "total_results", count);
        } else {
            long count = productRepository.countByCompanyIdAndEstadoIsTrue(companyEntity.getId());
            Page<ProductEntity> listProducts = productRepository.findAllByCompanyIdAndEstadoIsTrueOrderByIdDesc(companyEntity.getId(), new PageRequest(pagenumber - 1, perpage));
            return ImmutableMap.of("results", listProducts, "total_results", count);
        }


    }

    private static <K, V> K getKey(Map<K, V> map, V value) {
        return map.keySet()
                .stream()
                .filter(key -> value.equals(map.get(key)))
                .findFirst().get();
    }

    private Map<String, Object> validateEntity(ProductEntity productEntity) {
        boolean res = false;
        String mnsag = "";
        if (productEntity.getCodigo().length() > 255) {
            mnsag += " Campo codigo extenso";
            res = true;
        }
        if (productEntity.getDescripcion().length() > 500) {
            mnsag += " Campo descripcion extenso";
            res = true;
        }
        if (productEntity.getMoneda().length() > 255) {
            mnsag += " Campo moneda extenso";
            res = true;
        }
        if (productEntity.getTipoAfectacion().length() > 255) {
            mnsag += " Campo tipo de afectacion extenso";
            res = true;
        }
        if (productEntity.getUnidadMedida().length() > 255) {
            mnsag += " Campo unidad de medida extenso";
            res = true;
        }

        return ImmutableMap.of("mensag", mnsag, "status", res);
    }

    public static boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    @Override
    public ProductEntity findById(Long id) {
        return productRepository.findById(id).get();
    }

    @Override
    public ProductEntity saveProduct(ProductEntity product, Boolean isEdit, UserPrincipal user) {

        CompanyEntity companyEntity = companyRepository.findByRuc(user.getRuc());
        if (isEdit) {
            ProductEntity productEntity = productRepository.findById(product.getId()).get();
            productEntity.setCodigo(product.getCodigo());
            productEntity.setDescripcion(product.getDescripcion());
            productEntity.setMoneda(product.getMoneda());
            productEntity.setTipoAfectacion(product.getTipoAfectacion());
            productEntity.setValorVentaConIgv(product.getValorVentaConIgv());
            productEntity.setValorVentaSinIgv(product.getValorVentaSinIgv());
            productEntity.setCompany(companyEntity);
            productEntity.setUnidadMedida(product.getUnidadMedida());
            productEntity.setUpdatedBy(user.getUsername());
            return productRepository.save(productEntity);
        } else {
            product.setCreatedBy(user.getUsername());
            product.setCompany(companyEntity);
            product.setEstado(true);
            return productRepository.save(product);
        }

    }

    @Override
    public void deleteProduct(Long id, String user) {
        ProductEntity productEntity = productRepository.findById(id).get();
        productEntity.setUpdatedBy(user);
        productEntity.setEstado(false);
        productRepository.save(productEntity);
    }
}
