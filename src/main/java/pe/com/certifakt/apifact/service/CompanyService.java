package pe.com.certifakt.apifact.service;

import pe.com.certifakt.apifact.model.CompanyEntity;
import pe.com.certifakt.apifact.model.CuentaEntity;

import java.util.List;

public interface CompanyService {

    List<String> getAllRucsActive();

    CompanyEntity getCompanyByRuc(String ruc);

    CompanyEntity actualizarDatosEmpresa(String ruc, CompanyEntity companyEntity);

    List<CuentaEntity> cuentasByRuc(String ruc);

    List<CuentaEntity> addCuentaByRuc(String ruc, CuentaEntity cuentaEntity);

    List<CuentaEntity> removeCuentaByRuc(String ruc, CuentaEntity cuentaEntity);

    CompanyEntity findByRuc(String rucEmisor);

    void getAllLimitCompany();
}
