package pe.com.certifakt.apifact.service;


import pe.com.certifakt.apifact.exception.ServiceException;
import pe.com.certifakt.apifact.model.UnitCode;

import java.util.List;
import java.util.Map;

public interface UnitCodeService {

    List<UnitCode> searchUnitCode(String text);

    Map<String, Object> getUnitCodesByCompany(String ruc);

    Map<String, Object> addUnitCode(String ruc, UnitCode unitCode) throws ServiceException;

    Map<String, Object> removeUnitCode(String ruc, Long idUnitCode) throws ServiceException;

    Map<String, Object> setDefaultUnitCode(String ruc, UnitCode unitCode) throws ServiceException;

    Map<String, Object> removeDefaultUnitCode(String ruc) throws ServiceException;
}
