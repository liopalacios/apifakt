package pe.com.certifakt.apifact.service;


import pe.com.certifakt.apifact.bean.UserResponse;
import pe.com.certifakt.apifact.model.ProductEntity;
import pe.com.certifakt.apifact.security.UserPrincipal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ProductService {

    List<ProductEntity> findAllByCompanyId(String ruc);

    ProductEntity findById(Long id);

    ProductEntity saveProduct(ProductEntity product, Boolean isEdit, UserPrincipal user);

    void deleteProduct(Long id, String user);

    public List<ProductEntity> findByCompanyIdAndProd(String ruc, String producto);

    Map<String, Object> valiteAll(List<ProductEntity> products);

    ArrayList<String> saveAll(List<ProductEntity> products, UserPrincipal user);

    Map<String, Object> findByCompanyId(int pagenumber, int perpage, UserPrincipal user, String filter);
}
