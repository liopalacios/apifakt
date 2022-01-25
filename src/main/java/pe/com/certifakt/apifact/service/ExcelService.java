package pe.com.certifakt.apifact.service;

import pe.com.certifakt.apifact.enums.TipoArchivoEnum;
import pe.com.certifakt.apifact.model.RegisterDownloadUploadEntity;
import pe.com.certifakt.apifact.model.RegisterFileUploadEntity;
import pe.com.certifakt.apifact.security.UserPrincipal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;


public interface ExcelService {

    ByteArrayInputStream CustomDownloadExcel(UserPrincipal user, String fechaEmisionDesde, String fechaEmisionHasta,
                                             String tipoComprobante, String serie) throws Exception;

    void registrarExcel(UserPrincipal user, String filtroDesde, String filtroHasta, String filtroTipoComprobante, String filtroSerie, String linkS3, ByteArrayInputStream stream);

    Map<String, Object> getExcels(UserPrincipal userResponse, Integer pageNumber, Integer perPage);



    ByteArrayInputStream downloadFileInvoice(Long id, TipoArchivoEnum tipoArchivoEnum);

    ByteArrayInputStream downloadFileStorage(RegisterDownloadUploadEntity fileStorage);

    void uploadFilexlsx(Integer codCompany, String tipoComprobante, ByteArrayInputStream stream, String nombreDocumento);

}
