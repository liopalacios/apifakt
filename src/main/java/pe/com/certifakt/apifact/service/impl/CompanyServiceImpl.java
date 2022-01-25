package pe.com.certifakt.apifact.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pe.com.certifakt.apifact.model.CompanyEntity;
import pe.com.certifakt.apifact.model.CuentaEntity;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.repository.CuentaRepository;
import pe.com.certifakt.apifact.service.CompanyService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final CuentaRepository cuentaRepository;

    @Override
    public List<String> getAllRucsActive() {

        return companyRepository.getCompaniesForSummaryDocuments();
    }


    @Override
    public CompanyEntity getCompanyByRuc(String ruc) {
        return companyRepository.findByRuc(ruc);
    }

    @Override
    public CompanyEntity findByRuc(String rucEmisor) {
        return companyRepository.findByRuc(rucEmisor);
    }

    @Override
    public void getAllLimitCompany() {
        Date date = new Date();
        List<CompanyEntity> allLimitCompany = companyRepository.getAllLimitCompany(date);
        for (CompanyEntity c: allLimitCompany ) {
            c.setEstado("D");
            companyRepository.save(c);
        }
    }

    @Override
    public CompanyEntity actualizarDatosEmpresa(String ruc, CompanyEntity empresa) {
        CompanyEntity companyEntity = companyRepository.findByRuc(ruc);
        companyEntity.setArchivoLogo(empresa.getArchivoLogo());
        companyEntity.setEmail(empresa.getEmail());
        companyEntity.setRazonSocial(empresa.getRazonSocial());
        companyEntity.setDireccion(empresa.getDireccion());
        companyEntity.setNombreComercial(empresa.getNombreComercial());
        companyEntity.setTelefono(empresa.getTelefono());
        companyEntity.setFormat(empresa.getFormat());
        companyEntity.setCantComprobanteDinamico(empresa.getCantComprobanteDinamico());
        companyEntity.setEnvioAutomaticoSunat(empresa.getEnvioAutomaticoSunat());
        companyEntity.setPreciosIncluidoIgv(empresa.getPreciosIncluidoIgv());
        companyEntity.setViewCode(empresa.getViewCode());
        companyEntity = companyRepository.save(companyEntity);
        return companyEntity;
    }

    @Override
    public List<CuentaEntity> cuentasByRuc(String ruc) {
        CompanyEntity companyEntity = companyRepository.findByRuc(ruc);
        return companyEntity.getCuentas();
    }

    @Override
    public List<CuentaEntity> addCuentaByRuc(String ruc, CuentaEntity cuentaEntity) {
        CompanyEntity companyEntity = companyRepository.findByRuc(ruc);
        companyEntity.addCuenta(cuentaEntity);
        companyRepository.save(companyEntity);

        return companyEntity.getCuentas();
    }

    @Override
    public List<CuentaEntity> removeCuentaByRuc(String ruc, CuentaEntity cuentaEntity) {
        CompanyEntity companyEntity = companyRepository.findByRuc(ruc);
        companyEntity.removeCuenta(cuentaEntity);
        companyRepository.save(companyEntity);
        cuentaRepository.deleteById(cuentaEntity.getId());

        return companyEntity.getCuentas();
    }
}
