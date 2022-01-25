package pe.com.certifakt.apifact.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pe.com.certifakt.apifact.dto.CatalogSunatDto;
import pe.com.certifakt.apifact.model.CatalogSunatEntity;
import pe.com.certifakt.apifact.repository.CatalogSunatRepository;
import pe.com.certifakt.apifact.service.CatalogoService;
import pe.com.certifakt.apifact.service.ParameterService;
import pe.com.certifakt.apifact.service.UbigeoService;
import pe.com.certifakt.apifact.service.UbigeototalService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CatalogoServiceImpl implements CatalogoService {

    private final CatalogSunatRepository catalogSunatRepository;

    private final UbigeoService ubigeoService;

    private final ParameterService parameterService;

    private final UbigeototalService ubigeototalService;

    @Override
    public List<CatalogSunatEntity> getListCatalog06() {
        return catalogSunatRepository.findByNumeroOrderByOrden("06");
    }

    @Override
    public List<CatalogSunatEntity> getListCatalog07() {
        return catalogSunatRepository.findByNumeroOrderByOrden("07");
    }

    @Override
    public List<CatalogSunatEntity> getListCatalog09() {
        return catalogSunatRepository.findByNumeroOrderByOrden("09");
    }

    @Override
    public List<CatalogSunatEntity> getListCatalog10() {
        return catalogSunatRepository.findByNumeroOrderByOrden("10");
    }

    @Override
    public List<CatalogSunatEntity> getListCatalog17() {
        return catalogSunatRepository.findByNumeroOrderByOrden("17");
    }

    @Override
    public List<CatalogSunatEntity> getListCatalog18() {
        return catalogSunatRepository.findByNumeroOrderByOrden("18");
    }

    @Override
    public List<CatalogSunatEntity> getListCatalog20() {
        return catalogSunatRepository.findByNumeroOrderByOrden("20");
    }

    @Override
    public List<CatalogSunatEntity> getListCatalog59() {
        return catalogSunatRepository.findByNumeroOrderByOrden("59");
    }

    @Override
    public List<CatalogSunatEntity> getListCatalog54() {
        return catalogSunatRepository.findByNumeroOrderByOrden("54");
    }

    @Override
    public Page<CatalogSunatEntity> getListCatalogo25ByDescripcion(String descripcion) {
        return catalogSunatRepository.findByNumeroAndDescripcion("25",descripcion.toUpperCase(), PageRequest.of(0,6));
    }

    @Override
    public Map<String, Object> getCatalogInicial() {
        Map<String, Object> data = new HashMap<>();
        List<String> nums = new ArrayList<>();
        nums.add("06");
        nums.add("07");
        nums.add("09");
        nums.add("10");
        nums.add("17");
        nums.add("18");
        nums.add("20");
        nums.add("59");
        nums.add("54");
        List<CatalogSunatEntity> entities = catalogSunatRepository.findByNumeroInOrderByNumero(nums);
        List<CatalogSunatDto> dtos = CatalogSunatDto.transformToDtoList(entities);

        data.put("catalogos",dtos);
        data.put("ubigeo", ubigeoService.findAllDepartamento());
        data.put("parametros", parameterService.getParametersList());
        data.put("ubigeoTotal", ubigeototalService.findAllUbigeo());
        return data;
    }

}
