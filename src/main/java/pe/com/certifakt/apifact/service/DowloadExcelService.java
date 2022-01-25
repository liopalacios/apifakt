package pe.com.certifakt.apifact.service;

import pe.com.certifakt.apifact.security.UserPrincipal;

public interface DowloadExcelService {

    public void registrarExcel(UserPrincipal user, String filtroDesde, String filtroHasta,String filtroTipoComprobante,String filtroSerie,String linkS3,String filtroEmail);
}
