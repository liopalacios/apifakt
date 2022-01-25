package pe.com.certifakt.apifact.service;

import org.springframework.data.domain.Page;
import pe.com.certifakt.apifact.bean.GuiaRemision;
import pe.com.certifakt.apifact.bean.InfoEstadoSunat;
import pe.com.certifakt.apifact.bean.OtherDocumentCpe;
import pe.com.certifakt.apifact.exception.ServiceException;
import pe.com.certifakt.apifact.model.GuiaRemisionEntity;
import pe.com.certifakt.apifact.model.OtherCpeEntity;
import pe.com.certifakt.apifact.security.UserPrincipal;

import java.util.List;
import java.util.Map;

public interface OthersVouchersService {

    Map<String, Object> getAllComprobantesByFiltersQuery(UserPrincipal userResponse, String fechaEmisionDesde,
                                                          String fechaEmisionHasta, String tipoComprobante, String numDocIdentReceptor, String serie, Integer numero,
                                                          Integer pageNumber, Integer perPage);

    Integer getSiguienteNumeroOtherCpe(String tipoDocumento, String serie, String ruc);

    List<InfoEstadoSunat> getEstadoSunatByListaIds(List<Long> ids);

    public Map<String, Object> generationOtherDocument(OtherDocumentCpe otherDocument, String authorization,Boolean isEdit, String userName);

    OtherCpeEntity prepareComprobanteForEnvioSunat(String ruc, String tipo, String serie, Integer numero) throws ServiceException;

    GuiaRemisionEntity prepareGuiaForEnvioSunat(String ruc, String serie, Integer numero) throws ServiceException;

    public Map<String, Object> generationGuiaRemision(GuiaRemision guiaRemision, String authorization, Boolean isEdit, String userName);
}
