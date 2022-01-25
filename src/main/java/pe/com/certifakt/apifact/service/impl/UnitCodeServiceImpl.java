package pe.com.certifakt.apifact.service.impl;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import pe.com.certifakt.apifact.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.certifakt.apifact.model.CompanyEntity;
import pe.com.certifakt.apifact.model.UnitCode;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.repository.UnitCodeRepository;
import pe.com.certifakt.apifact.service.UnitCodeService;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class UnitCodeServiceImpl implements UnitCodeService {

    private final UnitCodeRepository unitCodeRepository;
    private final CompanyRepository companyRepository;


    @Override
    public List<UnitCode> searchUnitCode(String text) {
        return unitCodeRepository.searchUnidades(text, text);
    }

    @Override
    @Transactional
    public Map<String, Object> getUnitCodesByCompany(String ruc) {
        CompanyEntity companyEntity = companyRepository.findByRuc(ruc);
        return ImmutableMap.of("unitCodes", companyEntity.getUnitCodes(), "default", companyEntity.getDefaultUnitCode() != null ? companyEntity.getDefaultUnitCode() : false);
    }

    @Override
    public Map<String, Object> addUnitCode(String ruc, UnitCode unitCode) throws ServiceException {
        CompanyEntity companyEntity = companyRepository.findByRuc(ruc);

        if (companyEntity.getUnitCodes().contains(unitCode))
            throw new ServiceException("Ya se encuentra agregado esta unidad de medida: " + unitCode.getCode());

        companyEntity.addUnitCode(unitCode);
        companyEntity = companyRepository.save(companyEntity);

        return ImmutableMap.of("unitCodes", companyEntity.getUnitCodes(), "default", companyEntity.getDefaultUnitCode() != null ? companyEntity.getDefaultUnitCode() : false);
    }

    @Override
    public Map<String, Object> removeUnitCode(String ruc, Long idUnitCode) throws ServiceException {
        CompanyEntity companyEntity = companyRepository.findByRuc(ruc);
        UnitCode unitCode = unitCodeRepository.findById(idUnitCode).get();
        if (!companyEntity.getUnitCodes().contains(unitCode)) {
            throw new ServiceException("No se encuentra en lista la unidad: " + unitCode.toString());
        } else {
            companyEntity.getUnitCodes().remove(unitCode);
            if (companyEntity.getDefaultUnitCode() != null && companyEntity.getDefaultUnitCode().equals(unitCode)) {
                companyEntity.setDefaultUnitCode(null);
            }
            companyEntity = companyRepository.save(companyEntity);
        }

        return ImmutableMap.of("unitCodes", companyEntity.getUnitCodes(), "default", companyEntity.getDefaultUnitCode() != null ? companyEntity.getDefaultUnitCode() : false);
    }

    @Override
    public Map<String, Object> setDefaultUnitCode(String ruc, UnitCode unitCode) throws ServiceException {
        CompanyEntity companyEntity = companyRepository.findByRuc(ruc);
        companyEntity.setDefaultUnitCode(unitCode);
        companyEntity = companyRepository.save(companyEntity);
        return ImmutableMap.of("unitCodes", companyEntity.getUnitCodes(), "default", companyEntity.getDefaultUnitCode() != null ? companyEntity.getDefaultUnitCode() : false);
    }

    @Override
    public Map<String, Object> removeDefaultUnitCode(String ruc) throws ServiceException {
        CompanyEntity companyEntity = companyRepository.findByRuc(ruc);
        companyEntity.setDefaultUnitCode(null);
        companyEntity = companyRepository.save(companyEntity);
        return ImmutableMap.of("unitCodes", companyEntity.getUnitCodes(), "default", companyEntity.getDefaultUnitCode() != null ? companyEntity.getDefaultUnitCode() : false);

    }
}
