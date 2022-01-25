package pe.com.certifakt.apifact.service;


import org.springframework.data.domain.Page;
import pe.com.certifakt.apifact.model.CatalogSunatEntity;

import java.util.List;
import java.util.Map;

public interface CatalogoService {

    List<CatalogSunatEntity> getListCatalog06();

    List<CatalogSunatEntity> getListCatalog07();

    List<CatalogSunatEntity> getListCatalog09();

    List<CatalogSunatEntity> getListCatalog10();

    List<CatalogSunatEntity> getListCatalog17();

    List<CatalogSunatEntity> getListCatalog18();

    List<CatalogSunatEntity> getListCatalog20();

    List<CatalogSunatEntity> getListCatalog59();

    List<CatalogSunatEntity> getListCatalog54();

    Page<CatalogSunatEntity> getListCatalogo25ByDescripcion(String descripcion);

    Map<String, Object> getCatalogInicial();
}
